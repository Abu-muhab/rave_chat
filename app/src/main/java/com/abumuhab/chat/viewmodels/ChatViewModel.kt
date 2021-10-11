package com.abumuhab.chat.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abumuhab.chat.database.UserDataDao
import com.abumuhab.chat.models.ChatPreview
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
    private val userDataDao: UserDataDao, preview: ChatPreview,
    private val application: Application
) : ViewModel() {
    private var socket: Socket? = null

    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?>
        get() = _userData

    var messages = MutableLiveData<ArrayList<Message>>()

    init {
        getLoggedInUser()

        messages.value = arrayListOf(
            Message(
                "hello",
                Calendar.getInstance().time,
                "abumuhab",
                preview.name
            ),
            Message(
                "Hii",
                Calendar.getInstance().time,
                preview.name,
                "abumuhab"
            ),
            Message(
                "i was wondering if we could work on soemthing",
                Calendar.getInstance().time,
                "abumuhab",
                preview.name
            ),
            Message(
                "i have this mad idea",
                Calendar.getInstance().time,
                "abumuhab",
                preview.name
            ),
            Message(
                "Let's hear it. i am excited about this my gee!!",
                Calendar.getInstance().time,
                preview.name,
                "abumuhab"
            ),
            Message(
                "I need something to work on",
                Calendar.getInstance().time,
                preview.name,
                "abumuhab"
            ),
            Message(
                "this should do it",
                Calendar.getInstance().time,
                preview.name,
                "abumuhab"
            ),
            Message(
                "I need something to work on. I need something to work on. I need something to work on. I need something to work on. I need something to work on",
                Calendar.getInstance().time,
                preview.name,
                "abumuhab"
            ),
        )
    }

    private fun getLoggedInUser() {
        viewModelScope.launch {
            _userData.value = userDataDao.getLoggedInUser()
            connectToChatSocket()
        }
    }

    fun sendMessage(message: Message) {
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
        socket = ChatSocketIO.getInstance(_userData.value!!.authToken,application)
        if (!socket!!.connected()) {
            socket!!.connect()
        }
    }
}