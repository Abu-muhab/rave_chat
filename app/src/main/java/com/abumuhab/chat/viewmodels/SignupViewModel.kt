package com.abumuhab.chat.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abumuhab.chat.database.UserDataDao
import com.abumuhab.chat.models.UserData
import kotlinx.coroutines.launch

class SignupViewModel(
    private val userDataDao: UserDataDao,
    application: Application
) : ViewModel() {

    private val _showSignupSpinner = MutableLiveData<Boolean>()
    val showSignupSpinner: LiveData<Boolean>
        get() = _showSignupSpinner

    private val _showSnapSignupSpinner = MutableLiveData<Boolean>()
    val showSnapSignupSpinner: LiveData<Boolean>
        get() = _showSnapSignupSpinner

    private val _loggedIn = MutableLiveData<Boolean>()
    val loggedIn: LiveData<Boolean>
        get() = _loggedIn

    init {
        _showSignupSpinner.value = false
        _showSnapSignupSpinner.value=false
        _loggedIn.value = false
        updateLoginStatus()
    }

    fun setShowSpinner(value: Boolean) {
        _showSignupSpinner.value = value
    }

    fun setShowSnapchatSpinner(value: Boolean) {
        _showSnapSignupSpinner.value = value
    }

    fun logIn(userData: UserData) {
        viewModelScope.launch {
            userDataDao.insert(userData)
            _loggedIn.value = true
        }
    }

    private fun updateLoginStatus() {
        viewModelScope.launch {
            val userData = userDataDao.getLoggedInUser()
            if (userData != null) {
                _loggedIn.value = true
            }
        }
    }
}