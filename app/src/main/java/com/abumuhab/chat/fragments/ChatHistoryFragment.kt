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
import androidx.navigation.findNavController
import com.abumuhab.chat.R
import com.abumuhab.chat.adapters.ChatPreviewAdapter
import com.abumuhab.chat.database.UserDatabase
import com.abumuhab.chat.databinding.AvatarBinding
import com.abumuhab.chat.databinding.FragmentChatHistoryBinding
import com.abumuhab.chat.viewmodels.FriendsViewModel
import com.abumuhab.chat.viewmodels.LoginViewModelFactory

class ChatHistoryFragment : Fragment() {
    private lateinit var viewModel: FriendsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentChatHistoryBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_chat_history, container, false)
        (activity as AppCompatActivity).supportActionBar?.hide()

        val application: Application = requireNotNull(this.activity).application
        val userDao = UserDatabase.getInstance(application).userDataDao
        val viewModelFactory = LoginViewModelFactory(userDao, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(FriendsViewModel::class.java)


        val adapter = ChatPreviewAdapter()

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

        binding.newChatButton.setOnClickListener {
            it.findNavController()
                .navigate(ChatHistoryFragmentDirections.actionChatHistoryFragmentToNewChatFragment())
        }

        binding.addFriendsButton.setOnClickListener {
            it.findNavController()
                .navigate(ChatHistoryFragmentDirections.actionChatHistoryFragmentToFindFriendsFragment())
        }

        binding.friendList.adapter = adapter

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        return binding.root
    }
}