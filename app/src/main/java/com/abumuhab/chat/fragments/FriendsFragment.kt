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
import com.abumuhab.chat.models.ChatPreview
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

        val avatars = arrayOf(
            R.drawable.avatar_1,
            R.drawable.avatar_16,
            R.drawable.avatar_22,
            R.drawable.avatar_31,
            R.drawable.avatar_33,
            R.drawable.avatar_1,
            R.drawable.avatar_4,
            R.drawable.avatar_5,
            R.drawable.avatar_6,
        )

        val previews = arrayOf(
            ChatPreview(
                R.drawable.avatar_1,
                "Emmy",
                "Yeah. I pushed to prod not long ago",
                "12:30"
            ),
            ChatPreview(
                R.drawable.avatar_16,
                "Jb",
                "just withdraw 2k",
                "12:30"
            ),
            ChatPreview(
                R.drawable.avatar_22,
                "Benu",
                "Yo. I came by but didn't meet you",
                "12:30"
            ),
            ChatPreview(
                R.drawable.avatar_31,
                "Abdallah",
                "The lecturer just came. hurry",
                "12:30"
            ),
            ChatPreview(
                R.drawable.avatar_33,
                "Jefferson",
                "Its all good man",
                "12:30"
            ),
            ChatPreview(
                R.drawable.avatar_1,
                "Arturo",
                "Yeah. I pushed to prod not long ago",
                "12:30"
            ),
            ChatPreview(
                R.drawable.avatar_16,
                "Tokyo",
                "just withdraw 2k",
                "12:30"
            ),
            ChatPreview(
                R.drawable.avatar_22,
                "Casper",
                "Yo. I came by but didn't meet you",
                "12:30"
            ),
            ChatPreview(
                R.drawable.avatar_31,
                "Julus",
                "The lecturer just came. hurry",
                "12:30"
            ),
            ChatPreview(
                R.drawable.avatar_33,
                "Romero",
                "Its all good man",
                "12:30"
            ),
        )

        avatars.forEach {
            val avatarBinding = AvatarBinding.inflate(inflater, binding.friendsPreviewLayout, false)
            avatarBinding.resourceId = it
            binding.lifecycleOwner = this
            binding.friendsPreviewLayout.addView(avatarBinding.root)
        }

        val adapter = FriendAdapter()

        adapter.submitList(previews.toList())

        binding.friendList.adapter=adapter

        binding.lifecycleOwner=this

        binding.viewModel = viewModel

        return binding.root
    }
}