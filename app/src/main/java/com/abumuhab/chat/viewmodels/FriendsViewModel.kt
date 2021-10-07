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
import kotlinx.coroutines.launch

class FriendsViewModel(
    private val userDataDao: UserDataDao,
    application: Application
) : ViewModel() {
    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?>
        get() = _userData

    var chats = ArrayList<ChatPreview>()

    init {
        getLoggedInUser()
        chats = arrayListOf(
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
        }
    }
}