package com.abumuhab.chat.models

import androidx.room.*
import java.util.*


@Entity(tableName = "messages_table", indices = [Index(value = ["id"], unique = true)])
@TypeConverters(DateConverter::class)
data class Message(
    @PrimaryKey(autoGenerate = true) var dbId: Long = 0L,
    var content: String,
    var time: Date?,
    var from: String,
    var to: String?,
    var read: Boolean? = true,
    var id: String?
) {
    override fun equals(other: Any?): Boolean {
        return try {
            id == (other as Message).id
        } catch (e: Exception) {
            false
        }
    }

    override fun hashCode(): Int {
        var result = dbId.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + (time?.hashCode() ?: 0)
        result = 31 * result + from.hashCode()
        result = 31 * result + (to?.hashCode() ?: 0)
        result = 31 * result + (read?.hashCode() ?: 0)
        result = 31 * result + (id?.hashCode() ?: 0)
        return result
    }
}


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