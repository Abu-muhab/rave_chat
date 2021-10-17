package com.abumuhab.chat.network

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.abumuhab.chat.R
import com.abumuhab.chat.database.UserDatabase
import com.abumuhab.chat.models.ChatPreview
import com.abumuhab.chat.models.Friend
import com.abumuhab.chat.models.Message
import com.abumuhab.chat.models.UserData
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.*
import java.net.URI
import java.util.*


class ChatSocketIO {
    companion object {
        var socket: Socket? = null
        var application: Application? = null

        fun getInstance(userData: UserData, application: Application): Socket {
            this.application = application
            if (socket == null) {
                val options = IO.Options.builder().setAuth(
                    mapOf(
                        "authToken" to userData.authToken
                    )
                ).build()
                socket = IO.socket(URI.create(BASE_URL_TEST + "chat"), options)

                socket!!.on("message") {
                    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory())
                        .add(Date::class.java, Rfc3339DateJsonAdapter()).build()
                    val jsonAdapter: JsonAdapter<MessagePayload> =
                        moshi.adapter(MessagePayload::class.java)

                    val messagePayload: MessagePayload? = jsonAdapter.fromJson(it[0].toString())

                    val messageDao = UserDatabase.getInstance(application).messageDao
                    val chatPreviewDao = UserDatabase.getInstance(application).chatPreviewDao

                    CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
                        val chatPreviews =
                            chatPreviewDao.findChatPreview(messagePayload!!.message.from)
                        var unread = messageDao.getUnreadMessages(
                            userData.user.userName,
                            messagePayload.message.from
                        )

                        if (chatPreviews.isEmpty()) {
                            val chatPreview = ChatPreview(
                                0L,
                                messagePayload.senderDetails!!,
                                messagePayload.message.content,
                                Calendar.getInstance().time,
                                unread.size + 1
                            )
                            chatPreviewDao.insert(chatPreview)
                        } else {
                            chatPreviews.first().lastMessage = messagePayload.message.content
                            chatPreviews.first().unread = unread.size + 1
                            chatPreviewDao.update(chatPreviews.first())
                        }

                        messagePayload.message.read = false
                        messageDao.insert(messagePayload.message)

                        unread = messageDao.getUnreadMessages(
                            userData.user.userName,
                            messagePayload.message.from
                        )

                        //send notification
                        createNotificationChannel()
                        val notification = NotificationCompat.Builder(application, "chat")
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
                            NotificationCompat.Builder(application, "chat")
                                .setSmallIcon(R.drawable.ic_baseline_chat_24)
                                .setContentTitle(messagePayload.message.from)
                                .setContentText(messagePayload.message.content)
                                .setGroupSummary(true)
                                .setGroup("chats")

                        val unreadChats = chatPreviewDao.getUnreadChats()
                        var totalMessages: Int = 0

                        unreadChats.forEach {
                            totalMessages += it.unread
                        }

                        groupSummaryNotification.setStyle(
                            NotificationCompat.InboxStyle().setSummaryText(
                                "You have $totalMessages unread messages from ${unreadChats.size} Chats"
                            )
                        )

                        with(NotificationManagerCompat.from(application.baseContext)) {
                            notify(unread.first().dbId.toInt(), notification.build())
                            notify(-200, groupSummaryNotification.build())
                        }
                    }
                }
            }
            return requireNotNull(socket)
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
                    getSystemService(
                        application!!.baseContext,
                        NotificationManager::class.java
                    ) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    data class MessagePayload(val type: String, val message: Message, val senderDetails: Friend?)

    class NotificationID {
        companion object {
            private var id: Int = 5
            fun getID(): Int {
                id += 1
                return id
            }
        }
    }
}