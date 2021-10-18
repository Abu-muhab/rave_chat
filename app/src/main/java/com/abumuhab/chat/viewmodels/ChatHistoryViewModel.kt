package com.abumuhab.chat.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.abumuhab.chat.database.ChatPreviewDao
import com.abumuhab.chat.database.UserDataDao
import com.abumuhab.chat.models.ChatPreview
import com.abumuhab.chat.models.UserData
import com.abumuhab.chat.network.ChatSocketIO
import com.google.firebase.messaging.FirebaseMessaging
import io.socket.client.Socket
import kotlinx.coroutines.launch

class ChatHistoryViewModel(
    private val chatPreviewDao: ChatPreviewDao,
    private val userDataDao: UserDataDao,
    private val application: Application
) : ViewModel() {
    private var socket: Socket? = null
    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?>
        get() = _userData

    var chats = MutableLiveData<ArrayList<ChatPreview>>()

    init {
        getLoggedInUser()

        chats.value = arrayListOf()
    }

    private fun getLoggedInUser() {
        viewModelScope.launch {
            _userData.value = userDataDao.getLoggedInUser()
            connectToChatSocket()
            loadMessages()
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            chatPreviewDao.getChatPreviews().observeForever {
                val c = arrayListOf<ChatPreview>()
                c.addAll(it.toList())
                c.reverse()
                chats.value = c
            }
        }
    }

    fun connectToChatSocket() {
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

    fun disconnectSocket() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    socket = ChatSocketIO.getInstance(_userData.value!!, it.result, application)
                    if (socket!!.connected()) {
                        socket!!.disconnect()
                    }
                }
            }
    }
}