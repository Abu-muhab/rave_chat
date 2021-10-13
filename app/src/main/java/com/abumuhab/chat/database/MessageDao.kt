package com.abumuhab.chat.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.abumuhab.chat.models.Message


@Dao
interface MessageDao {
    @Insert
    suspend fun insert(message: Message)

    @Query("SELECT * FROM  messages_table ORDER BY time DESC LIMIT 10")
    suspend fun getMessages(): List<Message>

    @Query("SELECT * FROM  messages_table ORDER BY time DESC LIMIT 1")
    fun getLatestMessage(): LiveData<Message>
}