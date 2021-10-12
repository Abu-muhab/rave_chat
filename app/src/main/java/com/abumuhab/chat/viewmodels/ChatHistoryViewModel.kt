package com.abumuhab.chat.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abumuhab.chat.R
import com.abumuhab.chat.database.UserDataDao
import com.abumuhab.chat.models.ChatPreview
import com.abumuhab.chat.models.Friend
import com.abumuhab.chat.models.UserData
import com.abumuhab.chat.network.ChatSocketIO
import io.socket.client.Socket
import kotlinx.coroutines.launch

class ChatHistoryViewModel(
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

        chats.value = arrayListOf(
            ChatPreview(
                R.drawable.avatar_1,
                Friend(
                    "emmy",
                    "@emmy",
                    null,
                    null
                ),
                "Yeah. I pushed to prod not long ago",
                "12:30"
            ),
            ChatPreview(
                R.drawable.avatar_16,
                Friend(
                    "jb",
                    "@jb",
                    null,
                    null
                ),
                "just withdraw 2k",
                "12:30"
            ),
            ChatPreview(
                R.drawable.avatar_22,
                Friend(
                    "Benu",
                    "@benu",
                    null,
                    null
                ),
                "Yo. I came by but didn't meet you",
                "12:30"
            ),
            ChatPreview(
                R.drawable.avatar_31,
                Friend(
                    "Abdallah",
                    "@abdallah",
                    null,
                    null
                ),
                "The lecturer just came. hurry",
                "12:30"
            ),
            ChatPreview(
                R.drawable.avatar_33,
                Friend(
                    "Jeff",
                    "@jeff",
                    null,
                    null
                ),
                "Its all good man",
                "12:30"
            ),
        )
    }

    private fun getLoggedInUser() {
        viewModelScope.launch {
            _userData.value = userDataDao.getLoggedInUser()
            connectToChatSocket()
        }
    }

    private fun connectToChatSocket() {
        socket = ChatSocketIO.getInstance(_userData.value!!.authToken, application)
        if (!socket!!.connected()) {
            socket!!.connect()
        }
    }
}