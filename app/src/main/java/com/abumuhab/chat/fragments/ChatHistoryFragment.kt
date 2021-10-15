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
import androidx.recyclerview.widget.LinearLayoutManager
import com.abumuhab.chat.R
import com.abumuhab.chat.adapters.ChatPreviewAdapter
import com.abumuhab.chat.database.UserDatabase
import com.abumuhab.chat.databinding.AvatarBinding
import com.abumuhab.chat.databinding.FragmentChatHistoryBinding
import com.abumuhab.chat.viewmodels.ChatHistoryViewModel
import com.abumuhab.chat.viewmodels.ChatHistoryViewModelFactory
import com.abumuhab.chat.viewmodels.LoginViewModelFactory

class ChatHistoryFragment : Fragment() {
    private lateinit var viewModel: ChatHistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentChatHistoryBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_chat_history, container, false)
        (activity as AppCompatActivity).supportActionBar?.hide()

        val application: Application = requireNotNull(this.activity).application
        val userDao = UserDatabase.getInstance(application).userDataDao
        val chatPreviewDao = UserDatabase.getInstance(application).chatPreviewDao
        val viewModelFactory = ChatHistoryViewModelFactory(chatPreviewDao, userDao, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ChatHistoryViewModel::class.java)


        val adapter = ChatPreviewAdapter()
        binding.friendList.adapter = adapter
        binding.friendList.itemAnimator = null

        viewModel.chats.observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.submitList(it.toList()) {
                    (binding.friendList.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                        it.size - 1,
                        0
                    )
                }
//                it.forEach {
//                    val avatarBinding =
//                        AvatarBinding.inflate(inflater, binding.friendsPreviewLayout, false)
//                    binding.lifecycleOwner = this
//                    binding.friendsPreviewLayout.addView(avatarBinding.root)
//                }
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

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        return binding.root
    }
}