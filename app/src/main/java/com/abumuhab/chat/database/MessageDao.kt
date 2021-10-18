package com.abumuhab.chat.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.abumuhab.chat.models.Message


@Dao
interface MessageDao {
    @Insert
    suspend fun insert(message: Message)

    @Update
    suspend fun update(message: Message)

    @Query("SELECT * FROM  messages_table WHERE (`from`=:currentUser AND `to`=:friend) OR (`from`=:friend AND `to`=:currentUser) ORDER BY time DESC LIMIT 20 ")
    suspend fun getMessages(currentUser: String, friend: String): List<Message>

    @Query("SELECT * FROM  messages_table WHERE `from`=:friend AND `to`=:currentUser AND `read`=0 ORDER BY time ASC")
    suspend fun getUnreadMessages(currentUser: String, friend: String): List<Message>

    @Query("SELECT * FROM  messages_table WHERE (`from`=:currentUser AND `to`=:friend) OR (`from`=:friend AND `to`=:currentUser) ORDER BY time DESC LIMIT 1")
    fun getLatestMessage(currentUser: String, friend: String): LiveData<Message>
}