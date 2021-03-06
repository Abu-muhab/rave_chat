package com.abumuhab.chat.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abumuhab.chat.database.ChatPreviewDao
import com.abumuhab.chat.database.MessageDao
import com.abumuhab.chat.database.UserDataDao
import com.abumuhab.chat.models.ChatPreview
import com.abumuhab.chat.models.Friend
import com.abumuhab.chat.models.UserData

class ChatViewModelFactory(
    private val chatPreviewDao: ChatPreviewDao,
    private val messageDao: MessageDao,
    private val userDao: UserDataDao,
    private val friend: Friend,
    private val application: Application,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(chatPreviewDao,messageDao,userDao, friend,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}