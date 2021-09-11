package com.abumuhab.chat.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abumuhab.chat.database.UserDataDao

class SignupViewModelFactory(
    private val userDao: UserDataDao,
    private val application: Application
):ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SignupViewModel::class.java)){
            return SignupViewModel(userDao,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}