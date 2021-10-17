package com.abumuhab.chat.models

import androidx.room.*
import java.util.*


@Entity(tableName = "messages_table",indices = [Index(value = ["id"],unique = true)])
@TypeConverters(DateConverter::class)
data class Message (
    @PrimaryKey(autoGenerate = true) var dbId: Long = 0L,
    var content: String,
    var time: Date?,
    var from: String,
    var to: String?,
    var read: Boolean?=true,
    var id: String?
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