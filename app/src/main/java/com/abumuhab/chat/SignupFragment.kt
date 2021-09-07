package com.abumuhab.chat

import android.app.Activity
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.abumuhab.chat.databinding.FragmentSignupBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient


class SignupFragment : Fragment() {
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var signUpRequest: BeginSignInRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        }

        binding.loginText.setOnClickListener {
            it.findNavController()
                .navigate(SignupFragmentDirections.actionSignupFragmentToLoginFragment())
        }

        binding.signupWithGoogle.setOnClickListener {
            oneTapAuth()
        }

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
                    .setServerClientId("447045430784-kll64g5872glhok59lb6h1c5a9okdoin.apps.googleusercontent.com")
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId("447045430784-kll64g5872glhok59lb6h1c5a9okdoin.apps.googleusercontent.com")
                    // Show all accounts on the device.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build()


        return binding.root
    }

    //TODO: refractor
    private fun oneTapAuth() {
        oneTapClient.beginSignIn(signInRequest).addOnSuccessListener {
            try {
                Log.i("CLASS", "here")
                startIntentSenderForResult(it.pendingIntent.intentSender, 1, null, 0, 0, 0, null)
            } catch (e: IntentSender.SendIntentException) {
                Log.i("CLASS", "Could not start oneTap UI")
            }
        }.addOnFailureListener{e1->
            Log.i("CLASS", e1.localizedMessage)
            oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener{
                    try {
                        startIntentSenderForResult(
                            it.pendingIntent.intentSender, 2,
                            null, 0, 0, 0,null)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e("CLASS", "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener{ e2->
                    // No Google Accounts found. Just continue presenting the signed-out UI.
                    Log.d("Class", e2.localizedMessage)
                }

        }
    }


}