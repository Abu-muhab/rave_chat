package com.abumuhab.chat.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.abumuhab.chat.database.ChatPreviewDao
import com.abumuhab.chat.database.MessageDao
import com.abumuhab.chat.database.UserDataDao
import com.abumuhab.chat.models.ChatPreview
import com.abumuhab.chat.models.Friend
import com.abumuhab.chat.models.Message
import com.abumuhab.chat.models.UserData
import com.abumuhab.chat.network.ChatSocketIO
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.socket.client.Socket
import kotlinx.coroutines.launch
import java.util.*

class ChatViewModel(
    private val chatPreviewDao: ChatPreviewDao,
    private val messageDao: MessageDao,
    private val userDataDao: UserDataDao, public val friend: Friend,
    private val application: Application
) : ViewModel() {
    private var socket: Socket? = null
    var observer: androidx.lifecycle.Observer<Message>? = null

    var messages = MutableLiveData<ArrayList<Message>>()
    var latestMessage = MutableLiveData<Message>()

    init {
        messages.value = arrayListOf()
    }

    private suspend fun markMessagesAsRead(userData: UserData) {
        //chat opened. mark all unread messages as read
        messageDao.getUnreadMessages(userData.user.userName, friend.userName!!)
            .forEach {
                it.read = true
                messageDao.update(it)
            }

        if (userData.user.userName != friend.userName) {
            chatPreviewDao.findChatPreview(friend.userName).apply {
                if (this.isNotEmpty()) {
                    this.first().unread = 0
                    chatPreviewDao.update(this.first())
                }
            }
        }
    }

    fun listenForNewMessages(lifecycleOwner: LifecycleOwner, userData: UserData) {
        viewModelScope.launch {
            observer = androidx.lifecycle.Observer<Message> {
                if (it != null && !messages.value!!.contains(it)) {
                    latestMessage.value = it
                    messages.value!!.add(it)
                    messages.value = messages.value

                    //mark as read
                    it.read = true
                    viewModelScope.launch {
                        messageDao.update(it)
                        if (it.from != userData.user.userName) {
                            val preview = chatPreviewDao.findChatPreview(it.from)
                            if (preview.isNotEmpty()) {
                                preview.first().lastMessage = it.content
                                preview.first().unread =
                                    messageDao.getUnreadMessages(
                                        userData.user.userName,
                                        it.from
                                    ).size

                                chatPreviewDao.update(preview.first())
                            }
                        }
                    }
                }
            }
            messageDao.getLatestMessage(
                userData.user.userName,
                friend.userName.toString()
            ).observe(lifecycleOwner, observer!!)
        }
    }


    suspend fun loadMessages(userData: UserData) {
        val array = arrayListOf<Message>()
        array.addAll(
            messageDao.getMessages(
                userData.user.userName,
                friend.userName.toString()
            ).toList()
        )
        if (array.size > 0) {
            array.reverse()
            array.removeLast()
            messages.value = array
        }
    }

    fun sendMessage(message: Message, userData: UserData) {
        viewModelScope.launch {
            message.read = true
            messageDao.insert(message)

            //update or create the chat preview
            val preview = chatPreviewDao.findChatPreview(friend.userName!!)
            if (preview.isNotEmpty()) {
                preview.first().lastMessage = message.content
                preview.first().unread =
                    messageDao.getUnreadMessages(
                        userData.user.userName,
                        friend.userName
                    ).size
                chatPreviewDao.update(preview.first())
            } else {
                val chatPreview = ChatPreview(
                    0L,
                    friend,
                    message.content,
                    Calendar.getInstance().time,
                    0
                )
                chatPreviewDao.insert(chatPreview)
            }
        }
        if (socket != null) {
            val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory())
                .add(Date::class.java, Rfc3339DateJsonAdapter()).build()
            val jsonAdapter: JsonAdapter<ChatSocketIO.MessagePayload> =
                moshi.adapter(ChatSocketIO.MessagePayload::class.java)
            socket!!.emit(
                "message", jsonAdapter.toJson(
                    ChatSocketIO.MessagePayload(
                        "new-chat",
                        message,
                        null
                    )
                )
            )
        }
    }

    suspend fun connectToChatSocket(userData: UserData, lifecycleOwner: LifecycleOwner) {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    socket = ChatSocketIO.getInstance(userData, it.result, application)
                    if (!socket!!.connected()) {
                        socket!!.connect()
                    }
                }
            }

        loadMessages(userData)
        listenForNewMessages(lifecycleOwner, userData)
        markMessagesAsRead(userData)
    }

    fun disconnectSocket(lifecycleOwner: LifecycleOwner, userData: UserData) {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    socket = ChatSocketIO.getInstance(userData, it.result, application)
                    if (socket!!.connected()) {
                        socket!!.disconnect()
                    }
                }
            }

        //stop listening for new messages
        messageDao.getLatestMessage(
            userData.user.userName,
            friend.userName.toString()
        ).removeObservers(lifecycleOwner)
    }
}