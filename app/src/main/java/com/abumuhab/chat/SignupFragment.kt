package com.abumuhab.chat

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.abumuhab.chat.databinding.FragmentSignupBinding


class SignupFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSignupBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_signup, container, false)
        (activity as AppCompatActivity).supportActionBar?.hide()

        binding.signupContainer.setOnClickListener {
            val imm = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken,0)
        }

        binding.loginText.setOnClickListener {
            it.findNavController().navigate(SignupFragmentDirections.actionSignupFragmentToLoginFragment())
        }

        return  binding.root
    }
}