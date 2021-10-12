package com.abumuhab.chat.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abumuhab.chat.R
import com.abumuhab.chat.database.UserDataDao
import com.abumuhab.chat.models.Friend
import com.abumuhab.chat.models.UserData
import com.abumuhab.chat.network.SearchResponse
import com.abumuhab.chat.network.UserAPi
import com.abumuhab.chat.util.showBasicMessageDialog
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindFriendsViewModel(private val userDataDao: UserDataDao, application: Application) :
    ViewModel() {
    val friends = MutableLiveData<ArrayList<Friend>>()
    val showLoadingIndicator = MutableLiveData<Boolean>()
    val showRetryMessage = MutableLiveData<Boolean>()
    val showNoMatch = MutableLiveData<Boolean>()
    private val userData = MutableLiveData<UserData?>()

    init {
        getLoggedInUser()
        showLoadingIndicator.value = false
        showRetryMessage.value = false
        showNoMatch.value = false

//        friends.value = arrayListOf(
//            Friend(
//                "Abdulmalik",
//                "@abumuhab", R.drawable.avatar_1,
//                null
//            ),
//            Friend(
//                "Umaymah",
//                "@_umaymahS", R.drawable.avatar_1,
//                null
//            )
//        )
    }

    private fun getLoggedInUser() {
        viewModelScope.launch {
            userData.value = userDataDao.getLoggedInUser()
        }
    }

    fun findFriends(limit: Int, page: Int, query: String) {
        viewModelScope.launch {
            if (userData.value != null) {
                showLoadingIndicator.value = true
                showRetryMessage.value = false
                UserAPi.retrofitService.search(userData.value!!.authToken, limit, page, query)
                    .enqueue(
                        object : Callback<SearchResponse> {
                            override fun onResponse(
                                call: Call<SearchResponse>,
                                response: Response<SearchResponse>
                            ) {
                                if (response.body()!!.status) {
                                    friends.value =
                                        response.body()!!.data!!.users as ArrayList<Friend>
                                    Log.i("FRINDS", friends.value!!.size.toString())
                                } else {
                                    showRetryMessage.value = true;
                                }
                                showLoadingIndicator.value = false
                            }

                            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                                Log.i("FAIL", t.message.toString())
                                showLoadingIndicator.value = false
                                showRetryMessage.value = true
                            }
                        }
                    )
            }
        }
    }
}