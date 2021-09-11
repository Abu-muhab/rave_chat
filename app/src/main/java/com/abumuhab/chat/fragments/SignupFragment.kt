package com.abumuhab.chat.fragments

import android.app.Activity
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
import com.abumuhab.chat.R
import com.abumuhab.chat.databinding.FragmentSignupBinding
import com.abumuhab.chat.network.*
import com.abumuhab.chat.viewmodels.SignupViewModel
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
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var signUpRequest: BeginSignInRequest

    private lateinit var startForResult: ActivityResultLauncher<IntentSenderRequest>


    private val viewModel: SignupViewModel by lazy{
        ViewModelProvider(this).get(SignupViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startForResult =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val credential = oneTapClient.getSignInCredentialFromIntent(it.data)
                    val idToken = credential.googleIdToken
                    val username = credential.id
                    val password = credential.password
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSignupBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false)
        (activity as AppCompatActivity).supportActionBar?.hide()

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

        binding.signupWithGoogle.setOnClickListener {
            oneTapAuth()
        }

        binding.signupButton.setOnClickListener {
            if(validateFields(binding)){
                Log.i("HHH","Form is valid")
                viewModel.setShowSpinner(true)
                lifecycleScope.launch {
                    signup(binding.emailField.text.toString().trim(),binding.passwordField.text.toString())
                }
            }
        }

        binding.viewModel = viewModel

        oneTapClient = Identity.getSignInClient((activity as AppCompatActivity))
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true).build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(true)
                    .setServerClientId("447045430784-5cknb7uqibjcnsrn39776plhkvugl6lt.apps.googleusercontent.com")
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId("447045430784-5cknb7uqibjcnsrn39776plhkvugl6lt.apps.googleusercontent.com")
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()


        binding.lifecycleOwner=this
        return binding.root
    }


    private fun validateFields(binding: FragmentSignupBinding): Boolean{
        var  formIsValid = true

        val emailFieldError: String? = validateEmailField(binding.emailField.text.toString().trim())

        if(emailFieldError!=null){
            showError(binding.emailFieldLayout,emailFieldError)
            formIsValid=false
        }else{
            removeError(binding.emailFieldLayout)
        }

        if(binding.passwordField.text!!.length<6){
            showError(binding.passwordFieldLayout,"Password too short")
            formIsValid=false
        }else{
            removeError(binding.passwordFieldLayout)
        }

        return  formIsValid
    }

    private fun showError(input: TextInputLayout,message: String){
        input.isErrorEnabled=true
        input.error=message
    }

    private fun removeError(input: TextInputLayout){
        input.isErrorEnabled=false
        input.error=null;
    }


    private fun validateEmailField(value: String): String?{
        if(value.isEmpty()){
            return  "Email Cannot be empty"
        }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()){
            return  "Not a valid email address"
        }
        return null
    }

    private fun signup(email:String,password: String){
        val payload= AuthPayload(email,password)
        val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        AuthApi.retrofitService.signup(payload).enqueue(
            object: Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.i("AUTH",response.code().toString())
                    if(response.code()==200){
                        val jsonAdapter: JsonAdapter<AuthSuccessResponse> = moshi.adapter(AuthSuccessResponse::class.java)
                        val authResponse: AuthSuccessResponse = jsonAdapter.fromJson(response.body().toString())!!
                    }else{
                        val jsonAdapter: JsonAdapter<AuthErrorResponse> = moshi.adapter(AuthErrorResponse::class.java)
                        val authResponse: AuthErrorResponse = jsonAdapter.fromJson(response.errorBody()?.string().toString())!!
                    }
                    viewModel.setShowSpinner(false)
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    viewModel.setShowSpinner(false)
                }

            }
        )
    }


    //TODO: refractor
    private fun oneTapAuth() {
        oneTapClient.beginSignIn(signInRequest).addOnSuccessListener {
            try {
                Log.i("LOGIN", "here")
                startIntentSenderForResult(it.pendingIntent.intentSender, 1, null, 0, 0, 0, null)
            } catch (e: IntentSender.SendIntentException) {
                Log.i("LOGIN", "Could not start oneTap UI")
            }
        }.addOnFailureListener { e1 ->
            Log.i("LOGIN", e1.localizedMessage)
            oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener {
                    try {
                        startForResult.launch(
                            IntentSenderRequest.Builder(it.pendingIntent.intentSender).build()
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e("LOGIN", "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener { e2 ->
                    // No Google Accounts found. Just continue presenting the signed-out UI.
                    Log.d("LOGIN", e2.localizedMessage)
                }
        }
    }


}