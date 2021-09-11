package com.abumuhab.chat.models

import com.squareup.moshi.Json

class User(
    val email: String,
    @Json(name = "_id") val id: String
)