package com.abumuhab.chat.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.abumuhab.chat.models.UserData

@Dao
interface UserDataDao {
    @Update
    suspend fun update(userData: UserData)

    @Insert
    suspend fun insert(UserData: UserData)

    @Query("SELECT * FROM user_data_table ORDER BY dbId DESC LIMIT 1")
    suspend fun getLoggedInUser(): UserData?
}