package com.abumuhab.chat.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user_data_table")
data class UserData(
    @PrimaryKey(autoGenerate = true) var dbId: Long = 0L,
    @Embedded val user: User,
    @ColumnInfo(name = "auth_token") val authToken: String
)