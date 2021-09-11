package com.abumuhab.chat.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignupViewModel: ViewModel() {
    private val _showSignupSpinner = MutableLiveData<Boolean>()

    val showSignupSpinner: LiveData<Boolean>
        get()= _showSignupSpinner



    init{
        _showSignupSpinner.value = false
    }

    fun setShowSpinner(value: Boolean){
        _showSignupSpinner.value=value
    }
}