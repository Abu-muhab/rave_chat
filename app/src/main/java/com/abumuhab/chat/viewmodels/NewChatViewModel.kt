package com.abumuhab.chat.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import com.abumuhab.chat.database.UserDataDao

class NewChatViewModel(private val userDataDao: UserDataDao,application: Application):ViewModel() {
}