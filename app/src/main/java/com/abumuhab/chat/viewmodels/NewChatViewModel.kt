package com.abumuhab.chat.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abumuhab.chat.R
import com.abumuhab.chat.database.UserDataDao
import com.abumuhab.chat.models.Friend

class NewChatViewModel(private val userDataDao: UserDataDao,application: Application):ViewModel() {
    val friends = MutableLiveData<ArrayList<Friend>>()

    init {
        friends.value = arrayListOf(
            Friend(
                "Abdulmalik",
                "@abumuhab", R.drawable.avatar_1,
                null
            ),
            Friend(
                "Umaymah",
                "@_umaymahS", R.drawable.avatar_1,
                null
            )
        )
    }
}