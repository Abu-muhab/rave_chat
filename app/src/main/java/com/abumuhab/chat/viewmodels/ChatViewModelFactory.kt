package com.abumuhab.chat.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abumuhab.chat.database.UserDataDao
import com.abumuhab.chat.models.ChatPreview

class ChatViewModelFactory(
    private val userDao: UserDataDao,
    private val chatPreview: ChatPreview,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(userDao, chatPreview,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}