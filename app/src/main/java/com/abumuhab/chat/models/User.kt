package com.abumuhab.chat.models

import androidx.annotation.Nullable
import com.squareup.moshi.Json

class User(
    @Nullable val email: String?,
    @Json(name = "_id") val id: String,
    @Nullable val avatarUrl: String?,
    @Nullable val snapId: String?,
    @Nullable val displayName: String?,
    val userName: String
)