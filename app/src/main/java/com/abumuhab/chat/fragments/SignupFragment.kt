package com.abumuhab.chat.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.DialogInterface
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.abumuhab.chat.R
import com.abumuhab.chat.database.UserDatabase
import com.abumuhab.chat.databinding.FragmentSignupBinding
import com.abumuhab.chat.network.*
import com.abumuhab.chat.util.removeEditTextError
import com.abumuhab.chat.util.showBasicMessageDialog
import com.abumuhab.chat.util.showEditTextError
import com.abumuhab.chat.util.validateEmailField
import com.abumuhab.chat.viewmodels.SignupViewModel
import com.abumuhab.chat.viewmodels.SignupViewModelFactory
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.textfield.TextInputLayout
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class SignupFragment : Fragment() {
    private lateinit var viewModel: SignupViewModel

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