package com.abumuhab.chat.network

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
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
                        messagePayload!!.message.read=false
                        messageDao.insert(messagePayload.message)

                        val chatPreviews =
                            chatPreviewDao.findChatPreview(messagePayload.message.from)
                        val unread = messageDao.getUnreadMessages(
                            userData.user.userName,
                            messagePayload.message.from
                        )
                        Log.i("SIZEEEE",unread.size.toString())
                        if (chatPreviews.isEmpty()) {
                            val chatPreview = ChatPreview(
                                0L,
                                messagePayload.senderDetails!!,
                                messagePayload.message.content,
                                Calendar.getInstance().time,
                                unread.size
                            )
                            chatPreviewDao.insert(chatPreview)
                        } else {
                            chatPreviews.first().lastMessage = messagePayload.message.content
                            chatPreviews.first().unread = unread.size
                            chatPreviewDao.update(chatPreviews.first())
                        }
                    }

                    //send notification
                    createNotificationChannel()
                    val builder = NotificationCompat.Builder(application, "chat")
                        .setSmallIcon(R.drawable.ic_baseline_chat_24)
                        .setContentTitle("Messages")
                        .setContentText(messagePayload!!.message.content)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                    with(NotificationManagerCompat.from(application.baseContext)) {
                        // notificationId is a unique int for each notification that you must define
                        notify(1, builder.build())
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
}