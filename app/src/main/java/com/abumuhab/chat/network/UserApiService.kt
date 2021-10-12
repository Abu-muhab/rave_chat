package com.abumuhab.chat.network

import com.abumuhab.chat.models.Friend
import com.snapchat.kit.sdk.core.models.AuthToken
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*


private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

private val retrofit: Retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
//    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL_TEST)
    .build()

interface UserApiService {
    @GET("api/users/search")
    fun search(
        @Header("Authorization") authToken: String,
        @Query("limit") limit: Int,
        @Query("page") page:Int,
        @Query("query") query: String
    ): Call<SearchResponse>
}

object UserAPi {
    val retrofitService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }
}

data class SearchResponse(
    val status: Boolean,
    val data: SearchData?,
    val message: String?
)

data class SearchData(
    val currentPage: Int,
    val lastPage: Int,
    val hasNextPage: Boolean,
    val users: List<Friend>
)