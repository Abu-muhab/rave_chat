package com.abumuhab.chat.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.abumuhab.chat.models.ChatPreview

@Dao
interface ChatPreviewDao {
    @Insert
    suspend fun insert(chatPreview: ChatPreview)

    @Query("SELECT * FROM chats_table ORDER BY dateModified DESC LIMIT 1")
    suspend fun getChatPreviews(): List<ChatPreview>

    @Query("SELECT * FROM chats_table WHERE userName = :userName")
    suspend fun findChatPreview(userName: String): List<ChatPreview>


    @Query("SELECT * FROM chats_table")
    fun getLatestChatPreview(): LiveData<ChatPreview>
}