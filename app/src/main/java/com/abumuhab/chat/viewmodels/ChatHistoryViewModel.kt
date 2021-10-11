package com.abumuhab.chat.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abumuhab.chat.R
import com.abumuhab.chat.database.UserDataDao
import com.abumuhab.chat.models.ChatPreview
import com.abumuhab.chat.models.UserData
import com.abumuhab.chat.network.ChatSocketIO
import io.socket.client.Socket
import kotlinx.coroutines.launch

class ChatHistoryViewModel(
    private val userDataDao: UserDataDao,
    private val application: Application
) : ViewModel() {
    private var socket: Socket?=null

    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?>
        get() = _userData

    var chats = MutableLiveData<ArrayList<ChatPreview>>()

    init {
        getLoggedInUser()

        chats.value = arrayListOf(
            ChatPreview(
                R.drawable.avatar_1,
                "Emmy",
                "Yeah. I pushed to prod not long ago",
                "12:30"
            ),
            ChatPreview(
                R.drawable.avatar_16,
                "Jb",
                "just withdraw 2k",
                "12:30"
            ),
            ChatPreview(
                R.drawable.avatar_22,
                "Benu",
                "Yo. I came by but didn't meet you",
                "12:30"
            ),
            ChatPreview(
                R.drawable.avatar_31,
                "Abdallah",
                "The lecturer just came. hurry",
                "12:30"
            ),
            ChatPreview(
                R.drawable.avatar_33,
                "Jefferson",
                "Its all good man",
                "12:30"
            ),
            ChatPreview(
                R.drawable.avatar_1,
                "Arturo",
                "Yeah. I pushed to prod not long ago",
                "12:30"
            ),
            ChatPreview(
                R.drawable.avatar_16,
                "Tokyo",
                "just withdraw 2k",
                "12:30"
            ),
            ChatPreview(
                R.drawable.avatar_22,
                "Casper",
                "Yo. I came by but didn't meet you",
                "12:30"
            ),
            ChatPreview(
                R.drawable.avatar_31,
                "Julus",
                "The lecturer just came. hurry",
                "12:30"
            ),
            ChatPreview(
                R.drawable.avatar_33,
                "Romero",
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

    private fun connectToChatSocket(){
        socket = ChatSocketIO.getInstance(_userData.value!!.authToken,application)
        if(!socket!!.connected()){
            socket!!.connect()
        }
    }
}