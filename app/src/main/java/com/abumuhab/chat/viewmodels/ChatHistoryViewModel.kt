package com.abumuhab.chat.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abumuhab.chat.database.ChatPreviewDao
import com.abumuhab.chat.database.UserDataDao
import com.abumuhab.chat.models.ChatPreview
import com.abumuhab.chat.models.UserData
import com.abumuhab.chat.network.ChatSocketIO
import io.socket.client.Socket
import kotlinx.coroutines.launch

class ChatHistoryViewModel(
    private val chatPreviewDao: ChatPreviewDao,
    private val userDataDao: UserDataDao,
    private val application: Application
) : ViewModel() {
    private var socket: Socket? = null
    var observer: androidx.lifecycle.Observer<ChatPreview>? = null

    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?>
        get() = _userData

    var chats = MutableLiveData<ArrayList<ChatPreview>>()
    var latestChat = MutableLiveData<ChatPreview>()

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

    private fun listenForNewChats() {
        viewModelScope.launch {
            observer = androidx.lifecycle.Observer<ChatPreview> {
                if (it != null && !chats.value!!.contains(it)) {
                    latestChat.value = it
                    chats.value!!.add(it)
                    chats.value = chats.value
                }
            }
            chatPreviewDao.getLatestChatPreview().observeForever(observer!!)
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            val array = arrayListOf<ChatPreview>()
            array.addAll(
                chatPreviewDao.getChatPreviews().toList()
            )
            if (array.size > 0) {
                array.reverse()
                array.removeLast()
                chats.value = array
            }
            listenForNewChats()
        }
    }

    override fun onCleared() {
        latestChat.let {
            if (observer !== null) {
                it.removeObserver(observer!!)
            }
        }
        super.onCleared()
    }

    private fun connectToChatSocket() {
        socket = ChatSocketIO.getInstance(_userData.value!!.authToken, application)
        if (!socket!!.connected()) {
            socket!!.connect()
        }
    }
}