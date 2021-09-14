package com.abumuhab.chat.fragments

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.abumuhab.chat.R
import com.abumuhab.chat.database.UserDatabase
import com.abumuhab.chat.databinding.FragmentSignupBinding
import com.abumuhab.chat.network.AuthApi
import com.abumuhab.chat.network.AuthErrorResponse
import com.abumuhab.chat.network.AuthPayload
import com.abumuhab.chat.network.AuthSuccessResponse
import com.abumuhab.chat.util.removeEditTextError
import com.abumuhab.chat.util.showBasicMessageDialog
import com.abumuhab.chat.util.showEditTextError
import com.abumuhab.chat.util.validateEmailField
import com.abumuhab.chat.viewmodels.SignupViewModel
import com.abumuhab.chat.viewmodels.SignupViewModelFactory
import com.snapchat.kit.sdk.SnapKit
import com.snapchat.kit.sdk.SnapLogin
import com.snapchat.kit.sdk.core.controller.LoginStateController.OnLoginStateChangedListener
import com.snapchat.kit.sdk.login.models.UserDataResponse
import com.snapchat.kit.sdk.login.networking.FetchUserDataCallback
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignupFragment : Fragment() {
    private lateinit var viewModel: SignupViewModel
    private lateinit var mLoginStateChangedListener: OnLoginStateChangedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLoginStateChangedListener = object : OnLoginStateChangedListener {
            override fun onLoginSucceeded() {
                val query = "{me{bitmoji{avatar},displayName,externalId,bitmoji{selfie}}}"
                val variables = mapOf<String, Any>()

                lifecycleScope.launch {
                    SnapLogin.fetchUserData(
                        requireContext(),
                        query,
                        variables,
                        object : FetchUserDataCallback {
                            override fun onSuccess(userDataResponse: UserDataResponse?) {
                                if (userDataResponse == null || userDataResponse.data == null) {
                                    return
                                }
                                val meData = userDataResponse.data.me ?: return
                                val avatarUrl = meData.bitmojiData.selfie
                                val name = meData.displayName
                                val snapId = meData.externalId
                            }

                            override fun onFailure(isNetworkError: Boolean, statusCode: Int) {}
                        })
                }
            }

            override fun onLoginFailed() {}

            override fun onLogout() {}
        }

        SnapLogin.getLoginStateController(requireContext())
            .addOnLoginStateChangedListener(mLoginStateChangedListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentSignupBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false)
        (activity as AppCompatActivity).supportActionBar?.hide()

        val application: Application = requireNotNull(this.activity).application
        val userDao = UserDatabase.getInstance(application).userDataDao
        val viewModelFactory = SignupViewModelFactory(userDao, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SignupViewModel::class.java)

        binding.signupContainer.setOnClickListener {
            val imm =
                requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            it.clearFocus()
        }

        binding.loginText.setOnClickListener {
            it.findNavController()
                .navigate(SignupFragmentDirections.actionSignupFragmentToLoginFragment())
        }

        binding.signupButton.setOnClickListener {
            if (validateFields(binding)) {
                viewModel.setShowSpinner(true)
                lifecycleScope.launch {
                    signup(
                        binding.emailField.text.toString().trim(),
                        binding.passwordField.text.toString()
                    )
                }
            }
        }

        binding.signupWithSnapchat.setOnClickListener {
            SnapLogin.getAuthTokenManager(requireContext()).startTokenGrant()
        }

        viewModel.loggedIn.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.signupContainer.findNavController()
                    .navigate(SignupFragmentDirections.actionSignupFragmentToFriendsFragment())
            }
        }

        binding.viewModel = viewModel

        binding.lifecycleOwner = this
        return binding.root
    }


    private fun validateFields(binding: FragmentSignupBinding): Boolean {
        var formIsValid = true

        val emailFieldError: String? = validateEmailField(binding.emailField.text.toString().trim())

        if (emailFieldError != null) {
            showEditTextError(binding.emailFieldLayout, emailFieldError)
            formIsValid = false
        } else {
            removeEditTextError(binding.emailFieldLayout)
        }

        if (binding.passwordField.text!!.length < 6) {
            showEditTextError(binding.passwordFieldLayout, "Password too short")
            formIsValid = false
        } else {
            removeEditTextError(binding.passwordFieldLayout)
        }

        return formIsValid
    }

    private fun signup(email: String, password: String) {
        val payload = AuthPayload(email, password)
        val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter: JsonAdapter<AuthPayload> = moshi.adapter(AuthPayload::class.java)
        AuthApi.retrofitService.signup(jsonAdapter.toJson(payload)).enqueue(
            object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.code() == 200) {
                        val jsonAdapter: JsonAdapter<AuthSuccessResponse> =
                            moshi.adapter(AuthSuccessResponse::class.java)
                        val authResponse: AuthSuccessResponse =
                            jsonAdapter.fromJson(response.body().toString())!!
                        viewModel.logIn(authResponse.data)
                    } else {
                        val jsonAdapter: JsonAdapter<AuthErrorResponse> =
                            moshi.adapter(AuthErrorResponse::class.java)
                        val authResponse: AuthErrorResponse =
                            jsonAdapter.fromJson(response.errorBody()?.string().toString())!!
                        showBasicMessageDialog(authResponse.message, activity!!)
                    }
                    viewModel.setShowSpinner(false)
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    viewModel.setShowSpinner(false)
                }

            }
        )
    }
}