package com.abumuhab.chat.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abumuhab.chat.database.UserDataDao
import com.abumuhab.chat.models.UserData
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userDataDao: UserDataDao,
    application: Application
) : ViewModel() {

    private val _showLoginSpinner = MutableLiveData<Boolean>()
    val showLoginSpinner: LiveData<Boolean>
        get() = _showLoginSpinner

    private val _showSnapLoginSpinner = MutableLiveData<Boolean>()
    val showSnapLoginSpinner: LiveData<Boolean>
        get() = _showSnapLoginSpinner

    private val _loggedIn = MutableLiveData<Boolean>()
    val loggedIn: LiveData<Boolean>
        get() = _loggedIn

    init {
        _showLoginSpinner.value = false
        _showSnapLoginSpinner.value = false
        _loggedIn.value = false
        updateLoginStatus()
    }

    fun setShowSpinner(value: Boolean) {
        _showLoginSpinner.value = value
    }

    fun setShowSnapchatSpinner(value: Boolean) {
        _showSnapLoginSpinner.value = value
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