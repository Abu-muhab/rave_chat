package com.abumuhab.chat.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.abumuhab.chat.models.ChatPreview
import com.abumuhab.chat.models.Message
import com.abumuhab.chat.models.UserData


@Database(
    entities = [UserData::class, Message::class, ChatPreview::class],
    version = 10,
    exportSchema = false
)
abstract class UserDatabase : RoomDatabase() {
    abstract val userDataDao: UserDataDao
    abstract val messageDao: MessageDao
    abstract val chatPreviewDao: ChatPreviewDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        UserDatabase::class.java,
                        "user_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}