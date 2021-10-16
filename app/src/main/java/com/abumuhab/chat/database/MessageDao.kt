package com.abumuhab.chat.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.abumuhab.chat.models.Message


@Dao
interface MessageDao {
    @Insert
    suspend fun insert(message: Message)

    @Query("SELECT * FROM  messages_table WHERE (`from`=:currentUser AND `to`=:friend) OR (`from`=:friend AND `to`=:currentUser) ORDER BY dbId DESC LIMIT 20 ")
    suspend fun getMessages(currentUser: String, friend: String): List<Message>

    @Query("SELECT * FROM  messages_table WHERE (`from`=:currentUser AND `to`=:friend AND NOT read) OR (`from`=:friend AND `to`=:currentUser AND NOT read)")
    suspend fun getUnreadMessages(currentUser: String, friend: String): List<Message>

    @Query("SELECT * FROM  messages_table WHERE (`from`=:currentUser AND `to`=:friend) OR (`from`=:friend AND `to`=:currentUser) ORDER BY dbId DESC LIMIT 1")
    fun getLatestMessage(currentUser: String, friend: String): LiveData<Message>
}