package com.abumuhab.chat.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.work.*
import com.abumuhab.chat.network.ChatSocketIO
import com.abumuhab.chat.workers.MessageWorker
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.*

class RaveFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        if (message.data.isNotEmpty()) {
            val messageWorkRequest = OneTimeWorkRequest.Builder(MessageWorker::class.java)

            val data = Data.Builder()
            data.putString("data", message.data.getValue("message_details"))
            messageWorkRequest.setInputData(data.build())

            val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory())
                .add(Date::class.java, Rfc3339DateJsonAdapter()).build()
            val jsonAdapter: JsonAdapter<ChatSocketIO.MessagePayload> =
                moshi.adapter(ChatSocketIO.MessagePayload::class.java)

            val messagePayload: ChatSocketIO.MessagePayload? =
                jsonAdapter.fromJson(message.data.getValue("message_details"))

            WorkManager.getInstance(application).enqueueUniqueWork(
                messagePayload!!.message.id!!,
                ExistingWorkPolicy.KEEP, messageWorkRequest.build()
            )
        }
    }
}