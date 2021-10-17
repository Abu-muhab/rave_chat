package com.abumuhab.chat.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.abumuhab.chat.models.ChatPreview

@Dao
interface ChatPreviewDao {
    @Insert
    suspend fun insert(chatPreview: ChatPreview)

    @Update
    suspend fun update(chatPreview: ChatPreview)

    @Query("SELECT * FROM chats_table ORDER BY dateModified DESC")
    fun getChatPreviews(): LiveData<List<ChatPreview>>

    @Query("SELECT * FROM chats_table WHERE unread>0 ORDER BY dateModified DESC")
    suspend fun getUnreadChats(): List<ChatPreview>

    @Query("SELECT * FROM chats_table WHERE userName = :userName LIMIT 1")
    suspend fun findChatPreview(userName: String): List<ChatPreview>
}