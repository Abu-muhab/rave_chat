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
import com.abumuhab.chat.adapters.FriendAdapter
import com.abumuhab.chat.database.UserDatabase
import com.abumuhab.chat.databinding.FragmentFindFriendsBinding
import com.abumuhab.chat.viewmodels.FindFriendsViewModel
import com.abumuhab.chat.viewmodels.LoginViewModelFactory

class FindFriendsFragment : Fragment() {
    private lateinit var viewModel: FindFriendsViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentFindFriendsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_find_friends, container, false)
        (activity as AppCompatActivity).supportActionBar?.hide()

        val application: Application = requireNotNull(this.activity).application
        val userDao = UserDatabase.getInstance(application).userDataDao
        val viewModelFactory = LoginViewModelFactory(userDao, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(FindFriendsViewModel::class.java)


        val adapter = FriendAdapter()
        viewModel.friends.observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.submitList(it)
            }
        }
        binding.friendList.adapter = adapter

        binding.viewModel = viewModel
        return binding.root
    }
}