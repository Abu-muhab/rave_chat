package com.abumuhab.chat.fragments

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.abumuhab.chat.R
import com.abumuhab.chat.adapters.ChatAdapter
import com.abumuhab.chat.database.UserDatabase
import com.abumuhab.chat.databinding.FragmentChatBinding
import com.abumuhab.chat.models.Friend
import com.abumuhab.chat.models.Message
import com.abumuhab.chat.viewmodels.ChatViewModel
import com.abumuhab.chat.viewmodels.ChatViewModelFactory
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.*

class ChatFragment : Fragment() {
    private lateinit var viewModel: ChatViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentChatBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_chat, container, false)
        (activity as AppCompatActivity).supportActionBar?.hide()


        val args = ChatFragmentArgs.fromBundle(requireArguments())
        val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter: JsonAdapter<Friend> = moshi.adapter(Friend::class.java)
        val friend: Friend? = jsonAdapter.fromJson(args.friend)

        val application: Application = requireNotNull(this.activity).application
        val userDao = UserDatabase.getInstance(application).userDataDao
        val viewModelFactory = ChatViewModelFactory(userDao, friend!!, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ChatViewModel::class.java)

        binding.sendButton.setOnClickListener{
            Log.i("MSG",binding.messageBox.text.toString())
            viewModel.sendMessage(
                Message(
                    binding.messageBox.text.toString(),
                    Calendar.getInstance().time,
                    viewModel.userData.value!!.user.userName,
                    "@someuser"
            ))
        }

        binding.friend = friend

        val adapter = ChatAdapter()
        binding.messageList.adapter = adapter

        viewModel.messages.observe(viewLifecycleOwner) {
            it.reverse()
            ((binding.messageList.adapter as Any) as ChatAdapter).submitList(it)
            binding.messageList.post {
                binding.messageList.scrollToPosition(0)
            }
        }

        binding.lifecycleOwner = this

        return binding.root
    }
}