package com.abumuhab.chat.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abumuhab.chat.database.ChatPreviewDao
import com.abumuhab.chat.database.MessageDao
import com.abumuhab.chat.database.UserDataDao
import com.abumuhab.chat.models.ChatPreview
import com.abumuhab.chat.models.Friend
import com.abumuhab.chat.models.Message
import com.abumuhab.chat.models.UserData
import com.abumuhab.chat.network.ChatSocketIO
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.socket.client.Socket
import kotlinx.coroutines.launch
import java.util.*

class ChatViewModel(
    private val chatPreviewDao: ChatPreviewDao,
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
    var latestMessage = MutableLiveData<Message>()

    init {
        messages.value = arrayListOf()
        getLoggedInUser()
    }

    private fun getLoggedInUser() {
        viewModelScope.launch {
            _userData.value = userDataDao.getLoggedInUser()
            connectToChatSocket()
            loadMessages()

            //chat opened. mark all unread messages as read
            messageDao.getUnreadMessages(_userData.value!!.user.userName, friend.userName!!)
                .forEach {
                    it.read = true
                    messageDao.update(it)
                }

            if (_userData.value!!.user.userName != friend.userName) {
                chatPreviewDao.findChatPreview(friend.userName).apply {
                    if (this.isNotEmpty()) {
                        this.first().unread = 0
                        chatPreviewDao.update(this.first())
                    }
                }
            }
        }
    }

    private fun listenForNewMessages() {
        viewModelScope.launch {
            observer = androidx.lifecycle.Observer<Message> {
                if (it != null && !messages.value!!.contains(it)) {
                    latestMessage.value = it
                    messages.value!!.add(it)
                    messages.value = messages.value

                    //mark as read
                    it.read = true
                    viewModelScope.launch {
                        messageDao.update(it)
                        if (it.from != _userData.value!!.user.userName) {
                            val preview = chatPreviewDao.findChatPreview(it.from)
                            if (preview.isNotEmpty()) {
                                preview.first().lastMessage = it.content
                                preview.first().unread =
                                    messageDao.getUnreadMessages(
                                        _userData.value!!.user.userName,
                                        it.from
                                    ).size

                                chatPreviewDao.update(preview.first())
                            }
                        }
                    }
                }
            }
            messageDao.getLatestMessage(
                userData.value!!.user.userName,
                friend.userName.toString()
            ).observeForever(observer!!)
        }
    }

    override fun onCleared() {
        latestMessage.let {
            if (observer !== null) {
                it.removeObserver(observer!!)
            }
        }
        super.onCleared()
    }


    private fun loadMessages() {
        viewModelScope.launch {
            val array = arrayListOf<Message>()
            array.addAll(
                messageDao.getMessages(
                    _userData.value!!.user.userName,
                    friend.userName.toString()
                ).toList()
            )
            if (array.size > 0) {
                array.reverse()
                array.removeLast()
                messages.value = array
            }
            listenForNewMessages()
        }
    }

    fun sendMessage(message: Message) {
        viewModelScope.launch {
            message.read = true
            messageDao.insert(message)

            //update or create the chat preview
            val preview = chatPreviewDao.findChatPreview(friend.userName!!)
            if (preview.isNotEmpty()) {
                preview.first().lastMessage = message.content
                preview.first().unread =
                    messageDao.getUnreadMessages(
                        _userData.value!!.user.userName,
                        friend.userName
                    ).size
                chatPreviewDao.update(preview.first())
            } else {
                val chatPreview = ChatPreview(
                    0L,
                    friend,
                    message.content,
                    Calendar.getInstance().time,
                    0
                )
                chatPreviewDao.insert(chatPreview)
            }
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
                        message,
                        null
                    )
                )
            )
        }
    }

    private fun connectToChatSocket() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    socket = ChatSocketIO.getInstance(_userData.value!!, it.result, application)
                    if (!socket!!.connected()) {
                        socket!!.connect()
                    }
                }
            }
    }
}