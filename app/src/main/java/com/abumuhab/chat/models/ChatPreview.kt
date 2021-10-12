package com.abumuhab.chat.models

data class ChatPreview(
    val imageResource: Int,
    val friend: Friend,
    val lastMessage: String,
    val date: String,
)