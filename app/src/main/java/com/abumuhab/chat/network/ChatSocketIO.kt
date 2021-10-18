package com.abumuhab.chat.network

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.*
import com.abumuhab.chat.models.Friend
import com.abumuhab.chat.models.Message
import com.abumuhab.chat.models.UserData
import com.abumuhab.chat.workers.MessageWorker
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URI
import java.util.*


class ChatSocketIO {
    companion object {
        var socket: Socket? = null
        var application: Application? = null

        fun getInstance(userData: UserData, fcmToken: String, application: Application): Socket {
            this.application = application
            Log.i("FCM_TOKEN", fcmToken.toString())
            if (socket == null) {
                val options = IO.Options.builder().setAuth(
                    mapOf(
                        "authToken" to userData.authToken,
                        "fcmToken" to fcmToken
                    )
                ).build()
                socket = IO.socket(URI.create(BASE_URL + "chat"), options)

                socket!!.on("message") {
                    val messageWorkRequest = OneTimeWorkRequest.Builder(MessageWorker::class.java)

                    val data = Data.Builder()
                    data.putString("data", it[0].toString())
                    messageWorkRequest.setInputData(data.build())

                    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory())
                        .add(Date::class.java, Rfc3339DateJsonAdapter()).build()
                    val jsonAdapter: JsonAdapter<MessagePayload> =
                        moshi.adapter(MessagePayload::class.java)

                    val messagePayload: MessagePayload? =
                        jsonAdapter.fromJson(it[0].toString())

                    Log.i("KEY_KEY",messagePayload!!.message.id.toString())

                    WorkManager.getInstance(application).enqueueUniqueWork(
                        messagePayload!!.message.id!!,
                        ExistingWorkPolicy.KEEP, messageWorkRequest.build()
                    )
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