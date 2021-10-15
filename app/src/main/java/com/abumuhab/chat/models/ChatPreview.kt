package com.abumuhab.chat.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*

@Entity(tableName = "chats_table")
@TypeConverters(DateConverter::class)
data class ChatPreview(
    @PrimaryKey(autoGenerate = true) var dbId: Long = 0L,
    @Embedded val friend: Friend,
    val lastMessage: String,
    val dateModified: Date,
) {
    fun formattedDate(): String {
        return dateModified.day.toString() + "/" + dateModified.month.toShort() + "/" + dateModified.year.toString()
    }
}