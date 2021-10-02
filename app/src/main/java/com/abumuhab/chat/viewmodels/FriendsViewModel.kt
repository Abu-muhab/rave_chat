package com.abumuhab.chat.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abumuhab.chat.database.UserDataDao
import com.abumuhab.chat.models.UserData
import kotlinx.coroutines.launch

class FriendsViewModel(
    private val userDataDao: UserDataDao,
    application: Application
) : ViewModel() {
    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?>
        get() = _userData

    init {
        getLoggedInUser()
    }

    private fun getLoggedInUser() {
        viewModelScope.launch {
            _userData.value = userDataDao.getLoggedInUser()
        }
    }
}