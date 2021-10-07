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
import com.abumuhab.chat.databinding.AvatarBinding
import com.abumuhab.chat.databinding.FragmentFriendsBinding
import com.abumuhab.chat.viewmodels.FriendsViewModel
import com.abumuhab.chat.viewmodels.LoginViewModelFactory

class FriendsFragment : Fragment() {
    private lateinit var viewModel: FriendsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentFriendsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_friends, container, false)
        (activity as AppCompatActivity).supportActionBar?.hide()

        val application: Application = requireNotNull(this.activity).application
        val userDao = UserDatabase.getInstance(application).userDataDao
        val viewModelFactory = LoginViewModelFactory(userDao, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(FriendsViewModel::class.java)


        val adapter = FriendAdapter()

        viewModel.chats.observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.submitList(it)
                it.forEach {
                    val avatarBinding =
                        AvatarBinding.inflate(inflater, binding.friendsPreviewLayout, false)
                    avatarBinding.resourceId = it.imageResource
                    binding.lifecycleOwner = this
                    binding.friendsPreviewLayout.addView(avatarBinding.root)
                }
            }
        }

        binding.friendList.adapter = adapter

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        return binding.root
    }
}