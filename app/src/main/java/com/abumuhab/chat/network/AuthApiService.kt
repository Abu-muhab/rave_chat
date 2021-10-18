package com.abumuhab.chat.network

import com.abumuhab.chat.models.UserData
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*


const val BASE_URL = "https://rave-chat-api.herokuapp.com/"
//const val BASE_URL = "http://192.168.43.91:4000/"

//private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()


private val retrofit: Retrofit = Retrofit.Builder()
//    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface AuthApiService {
    @Headers("Content-Type: application/json")
    @POST("auth/users/signup")
    fun signup(@Body payload: String): Call<String>

    @Headers("Content-Type: application/json")
    @POST("auth/users/login")
    fun login(@Body payload: String): Call<String>

    @Headers("Content-Type: application/json")
    @POST("auth/users/snapchat")
    fun snapAuth(@Body payload: String): Call<String>
}


object AuthApi {
    val retrofitService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

}


class AuthSuccessResponse(
    val data: UserData,
    val status: Boolean
)

class AuthErrorResponse(
    val status: Boolean,
    val message: String
)

class AuthPayload(val email: String, val password: String,val name:String)

class SnapAuthPayload(val snapId:String,val avatarUrl: String, val displayName: String)