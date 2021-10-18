package com.abumuhab.chat.fragments

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.abumuhab.chat.R
import com.abumuhab.chat.database.UserDatabase
import com.abumuhab.chat.databinding.FragmentLoginBinding
import com.abumuhab.chat.network.*
import com.abumuhab.chat.util.removeEditTextError
import com.abumuhab.chat.util.showBasicMessageDialog
import com.abumuhab.chat.util.showEditTextError
import com.abumuhab.chat.util.validateEmailField
import com.abumuhab.chat.viewmodels.LoginViewModel
import com.abumuhab.chat.viewmodels.LoginViewModelFactory
import com.snapchat.kit.sdk.SnapLogin
import com.snapchat.kit.sdk.core.controller.LoginStateController
import com.snapchat.kit.sdk.login.models.UserDataResponse
import com.snapchat.kit.sdk.login.networking.FetchUserDataCallback
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class LoginFragment : Fragment() {
    private lateinit var viewModel: LoginViewModel
    private lateinit var mLoginStateChangedListener: LoginStateController.OnLoginStateChangedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLoginStateChangedListener = object : LoginStateController.OnLoginStateChangedListener {
            override fun onLoginSucceeded() {
                val query = "{me{bitmoji{avatar},displayName,externalId,bitmoji{selfie}}}"
                val variables = mapOf<String, Any>()
                viewModel.setShowSnapchatSpinner(true)
                lifecycleScope.launch {
                    try {
                        SnapLogin.fetchUserData(
                            requireContext(),
                            query,
                            variables,
                            object : FetchUserDataCallback {
                                override fun onSuccess(userDataResponse: UserDataResponse?) {
                                    if (userDataResponse == null || userDataResponse.data == null) {
                                        viewModel.setShowSnapchatSpinner(true)
                                        return
                                    }
                                    val meData = userDataResponse.data.me ?: return
                                    val avatarUrl = meData.bitmojiData.selfie
                                    val name = meData.displayName
                                    val snapId = meData.externalId

                                    snapSignup(snapId, avatarUrl, name)
                                }

                                override fun onFailure(isNetworkError: Boolean, statusCode: Int) {
                                    viewModel.setShowSnapchatSpinner(true)
                                }
                            })
                    } catch (exception: Exception) {
                        Log.e("MSG", exception.message.toString())
                    }
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
    ): View? {
        val binding: FragmentLoginBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login, container, false
        )
        (activity as AppCompatActivity).supportActionBar?.hide()

        val application: Application = requireNotNull(this.activity).application
        val userDao = UserDatabase.getInstance(application).userDataDao
        val viewModelFactory = LoginViewModelFactory(userDao, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)


        binding.loginContainer.setOnClickListener {
            val imm =
                requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            it.clearFocus()
        }

        binding.signupText.setOnClickListener {

            it.findNavController()
                .navigate(LoginFragmentDirections.actionLoginFragmentToSignupFragment())
        }

        binding.loginButton.setOnClickListener {
            if (validateFields(binding)) {
                viewModel.setShowSpinner(true)
                lifecycleScope.launch {
                    login(
                        binding.emailField.text.toString().trim(),
                        binding.passwordField.text.toString()
                    )
                }
            }
        }

        binding.loginWithSnapchat.setOnClickListener {
            SnapLogin.getAuthTokenManager(requireContext()).startTokenGrant()
        }

        viewModel.loggedIn.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.loginContainer.findNavController()
                    .navigate(LoginFragmentDirections.actionLoginFragmentToChatHistoryFragment())
            }
        }

        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        return binding.root
    }

    private fun validateFields(binding: FragmentLoginBinding): Boolean {
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

    private fun login(email: String, password: String) {
        val payload = AuthPayload(email, password,"")
        val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter: JsonAdapter<AuthPayload> = moshi.adapter(AuthPayload::class.java)
        AuthApi.retrofitService.login(jsonAdapter.toJson(payload)).enqueue(
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

    private fun snapSignup(snapId: String, avatarUrl: String, displayName: String) {
        val payload = SnapAuthPayload(snapId, avatarUrl, displayName)
        val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter: JsonAdapter<SnapAuthPayload> = moshi.adapter(SnapAuthPayload::class.java)
        AuthApi.retrofitService.snapAuth(jsonAdapter.toJson(payload)).enqueue(
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
                    viewModel.setShowSnapchatSpinner(false)
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    viewModel.setShowSnapchatSpinner(false)
                }

            }
        )
    }
}