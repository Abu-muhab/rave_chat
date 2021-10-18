package com.abumuhab.chat.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.abumuhab.chat.R
import com.abumuhab.chat.database.UserDatabase
import com.abumuhab.chat.models.ChatPreview
import com.abumuhab.chat.network.ChatSocketIO
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.Exception
import java.util.*

class MessageWorker(appContext: Context, private val workerParameters: WorkerParameters) :
    CoroutineWorker(appContext, workerParameters) {
    override suspend fun doWork(): Result {
        try {
            val userDao = UserDatabase.getInstance(applicationContext).userDataDao
            val chatPreviewDao = UserDatabase.getInstance(applicationContext).chatPreviewDao
            val messageDao = UserDatabase.getInstance(applicationContext).messageDao
            val userData = userDao.getLoggedInUser() ?: return Result.success()

            val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory())
                .add(Date::class.java, Rfc3339DateJsonAdapter()).build()
            val jsonAdapter: JsonAdapter<ChatSocketIO.MessagePayload> =
                moshi.adapter(ChatSocketIO.MessagePayload::class.java)

            val messagePayload: ChatSocketIO.MessagePayload? =
                jsonAdapter.fromJson(workerParameters.inputData.getString("data").toString())

            if (messageDao.getMessage(messagePayload!!.message.id!!) != null) return Result.success()

            val chatPreviews =
                chatPreviewDao.findChatPreview(messagePayload.message.from)
            var unread = messageDao.getUnreadMessages(
                userData.user.userName,
                messagePayload.message.from
            )

            messagePayload.message.read = false
            messageDao.insert(messagePayload.message)

            if (chatPreviews.isEmpty()) {
                val chatPreview = ChatPreview(
                    0L,
                    messagePayload.senderDetails!!,
                    messagePayload.message.content,
                    messagePayload.message.time!!,
                    unread.size + 1
                )
                chatPreviewDao.insert(chatPreview)
            } else {
                chatPreviews.first().lastMessage = messagePayload.message.content
                chatPreviews.first().unread = unread.size + 1
                chatPreviewDao.update(chatPreviews.first())
            }

            unread = messageDao.getUnreadMessages(
                userData.user.userName,
                messagePayload.message.from
            )

            //send notification
            createNotificationChannel()
            val notification = NotificationCompat.Builder(applicationContext, "chat")
                .setSmallIcon(R.drawable.ic_baseline_chat_24)
                .setContentTitle(messagePayload.message.from)
                .setContentText(messagePayload.message.content)
                .setGroup("chats")

            val style = NotificationCompat.InboxStyle()

            if (unread.size <= 5) {
                unread.forEach {
                    style.addLine(it.content)
                }
            } else {
                for (x in unread.size - 4..unread.size) {
                    style.addLine(unread[x - 1].content)
                }
            }

            notification.setStyle(style)

            val groupSummaryNotification =
                NotificationCompat.Builder(applicationContext, "chat")
                    .setSmallIcon(R.drawable.ic_baseline_chat_24)
                    .setGroupSummary(true)
                    .setGroup("chats")

            val unreadChats = chatPreviewDao.getUnreadChats()
            var totalMessages = 0

            unreadChats.forEach {
                totalMessages += it.unread
            }

            groupSummaryNotification.setStyle(
                NotificationCompat.InboxStyle().setSummaryText(
                    "$totalMessages messages from ${unreadChats.size} Chats"
                )
            )

            with(NotificationManagerCompat.from(applicationContext)) {
                notify(unread.first().dbId.toInt(), notification.build())
                notify(-200, groupSummaryNotification.build())
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "chat channel"
            val descriptionText = "messages channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("chat", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                ContextCompat.getSystemService(
                    applicationContext,
                    NotificationManager::class.java
                ) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}