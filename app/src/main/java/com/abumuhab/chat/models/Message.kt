package com.abumuhab.chat.models

import com.google.gson.annotations.JsonAdapter
import java.util.*

data class Message (
    val content: String,
    val time: Date,
    val from: String,
    val to: String?
)