package com.abumuhab.chat.network

import com.abumuhab.chat.models.User
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://rave-chat-api.herokuapp.com/"

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()


private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface AuthApiService {
    @Headers("Content-Type: application/json")
    @POST("auth/users/signup")
    fun signup(@Body payload: AuthPayload): Call<String>

    @Headers("Content-Type: application/json")
    @POST("auth/users/login")
    fun login(@Body payload: AuthPayload): Call<String>
}


object AuthApi{
    val retrofitService:  AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
}


class AuthSuccessResponse(
    val data: Data,
    val status: Boolean
)

class AuthErrorResponse(
    val status: Boolean,
    val message: String
)

class Data(
    val user: User,
    val authToken: String
)

class AuthPayload(val email:String, val password: String)