package com.abumuhab.chat.models

import androidx.annotation.Nullable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.*


@Entity(tableName = "messages_table")
@TypeConverters(DateConverter::class)
data class Message (
    @PrimaryKey(autoGenerate = true) var dbId: Long = 0L,
    var content: String,
    var time: Date?,
    var from: String,
    var to: String?,
    var read: Boolean?=true
)


object DateConverter {
    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return dateLong?.let { Date(it) }
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
}