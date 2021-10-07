package com.abumuhab.chat.fragments

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.abumuhab.chat.R
import com.abumuhab.chat.database.UserDatabase
import com.abumuhab.chat.databinding.FragmentNewChatBinding
import com.abumuhab.chat.viewmodels.LoginViewModelFactory
import com.abumuhab.chat.viewmodels.NewChatViewModel

class NewChatFragment : Fragment() {
    private lateinit var viewModel: NewChatViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentNewChatBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_new_chat, container, false)
        (activity as AppCompatActivity).supportActionBar?.hide()

        val application: Application = requireNotNull(this.activity).application
        val userDao = UserDatabase.getInstance(application).userDataDao
        val viewModelFactory = LoginViewModelFactory(userDao, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(NewChatViewModel::class.java)


        binding.viewModel = viewModel
        return binding.root
    }
}