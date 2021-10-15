package com.abumuhab.chat.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abumuhab.chat.database.ChatPreviewDao
import com.abumuhab.chat.database.UserDataDao

class ChatHistoryViewModelFactory(
    private val chatPreviewDao: ChatPreviewDao,
    private val userDao: UserDataDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatHistoryViewModel::class.java)) {
            return ChatHistoryViewModel(chatPreviewDao, userDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}