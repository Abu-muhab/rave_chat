package com.abumuhab.chat.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abumuhab.chat.database.UserDataDao
import com.abumuhab.chat.models.ChatPreview
import com.abumuhab.chat.models.Message
import com.abumuhab.chat.models.UserData
import com.abumuhab.chat.network.ChatSocketIO
import com.abumuhab.chat.util.showBasicMessageDialog
import kotlinx.coroutines.launch

class ChatViewModel(
    private val userDataDao: UserDataDao, preview: ChatPreview,
    application: Application
) : ViewModel() {
    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?>
        get() = _userData

    var messages = MutableLiveData<ArrayList<Message>>()

    init {
        getLoggedInUser()

        val socket = ChatSocketIO.getInstance()
        socket.on("connect") {
            Log.i("SOC", "socket connected")
        }

        socket.on("error") {
            Log.i("SOC", "socket error")
        }

        socket.connect()

        messages.value = arrayListOf(
            Message(
                "hello",
                "12:30",
                "abumuhab"
            ),
            Message(
                "Hii",
                "12:31",
                preview?.name!!
            ),
            Message(
                "i was wondering if we could work on soemthing",
                "12:30",
                "abumuhab"
            ),
            Message(
                "i have this mad idea",
                "12:30",
                "abumuhab"
            ),
            Message(
                "Let's hear it. i am excited about this my gee!!",
                "12:30",
                preview.name
            ),
            Message(
                "I need something to work on",
                "12:30",
                preview.name
            ),
            Message(
                "this should do it",
                "12:30",
                preview.name
            ),
            Message(
                "I need something to work on. I need something to work on. I need something to work on. I need something to work on. I need something to work on",
                "12:30",
                preview.name
            ),
        )
    }

    private fun getLoggedInUser() {
        viewModelScope.launch {
            _userData.value = userDataDao.getLoggedInUser()
        }
    }

    fun sendMessage(message: String){
        val socket= ChatSocketIO.getInstance()
        socket.emit("message","Hello, world")
    }
}