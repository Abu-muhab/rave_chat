package com.abumuhab.chat.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abumuhab.chat.database.MessageDao
import com.abumuhab.chat.database.UserDataDao
import com.abumuhab.chat.models.Friend
import com.abumuhab.chat.models.Message
import com.abumuhab.chat.models.UserData
import com.abumuhab.chat.network.ChatSocketIO
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.socket.client.Socket
import kotlinx.coroutines.launch
import java.util.*

class ChatViewModel(
    private val messageDao: MessageDao,
    private val userDataDao: UserDataDao, public val friend: Friend,
    private val application: Application
) : ViewModel() {
    private var socket: Socket? = null
    var observer: androidx.lifecycle.Observer<Message>? = null

    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?>
        get() = _userData

    var messages = MutableLiveData<ArrayList<Message>>()
    private var latestMessage: LiveData<Message>? = null

    init {
        messages.value = arrayListOf()
        getLoggedInUser()
    }

    private fun getLoggedInUser() {
        viewModelScope.launch {
            _userData.value = userDataDao.getLoggedInUser()
            connectToChatSocket()
            loadMessages()
//            listenForNewMessages()
        }
    }

    private fun listenForNewMessages() {
        viewModelScope.launch {
            latestMessage = messageDao.getLatestMessage()
            observer = androidx.lifecycle.Observer<Message> {
                it?.let { it ->
                    messages.value!!.add(it)
                    messages.value = messages.value
                }
            }
            latestMessage!!.observeForever(observer!!)
        }
    }

    override fun onCleared() {
        super.onCleared()
        latestMessage?.let {
            if(observer!==null){
                it.removeObserver(observer!!)
            }
        }
    }


    private fun loadMessages() {
        viewModelScope.launch {
            val array = arrayListOf<Message>()
            array.addAll(messageDao.getMessages().toList())
            array.reverse()
            array.removeLast()
            messages.value = array
            listenForNewMessages()
        }
    }

    private fun loadDummyMessages() {
        messages.value = arrayListOf(
            Message(
                0L,
                "hello",
                Calendar.getInstance().time,
                _userData.value!!.user.userName,
                null
            )
        )
    }

    fun sendMessage(message: Message) {
        viewModelScope.launch {
            messageDao.insert(message)
        }
        if (socket != null) {
            val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory())
                .add(Date::class.java, Rfc3339DateJsonAdapter()).build()
            val jsonAdapter: JsonAdapter<ChatSocketIO.MessagePayload> =
                moshi.adapter(ChatSocketIO.MessagePayload::class.java)
            socket!!.emit(
                "message", jsonAdapter.toJson(
                    ChatSocketIO.MessagePayload(
                        "new-chat",
                        message
                    )
                )
            )
        }
    }

    private fun connectToChatSocket() {
        socket = ChatSocketIO.getInstance(_userData.value!!.authToken, application)
        if (!socket!!.connected()) {
            socket!!.connect()
        }
    }
}