package com.abumuhab.chat.models

import androidx.room.*
import java.util.*

@Entity(tableName = "chats_table",indices = [Index(value = ["userName"], unique = true)])
@TypeConverters(DateConverter::class)
data class ChatPreview(
    @PrimaryKey(autoGenerate = true) var dbId: Long = 0L,
    @Embedded var friend: Friend,
    var lastMessage: String,
    var dateModified: Date,
    var unread: Int
) {
    fun formattedDate(): String {
        return dateModified.day.toString() + "/" + dateModified.month.toShort() + "/" + dateModified.year.toString()
    }
}