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
import com.abumuhab.chat.models.ChatPreview
import com.abumuhab.chat.models.Message
import com.abumuhab.chat.network.AuthPayload
import com.abumuhab.chat.viewmodels.ChatHistoryViewModel
import com.abumuhab.chat.viewmodels.ChatViewModel
import com.abumuhab.chat.viewmodels.ChatViewModelFactory
import com.abumuhab.chat.viewmodels.LoginViewModelFactory
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

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
        val jsonAdapter: JsonAdapter<ChatPreview> = moshi.adapter(ChatPreview::class.java)
        val preview: ChatPreview? = jsonAdapter.fromJson(args.chatPreview)

        val application: Application = requireNotNull(this.activity).application
        val userDao = UserDatabase.getInstance(application).userDataDao
        val viewModelFactory = ChatViewModelFactory(userDao, preview!!, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ChatViewModel::class.java)

        binding.sendButton.setOnClickListener{
            Log.i("MSG",binding.messageBox.text.toString())
            viewModel.sendMessage(binding.messageBox.text.toString())
        }

        binding.preview = preview

        val adapter = ChatAdapter()
        viewModel.messages.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.messageList.adapter = adapter

        binding.lifecycleOwner = this

        return binding.root
    }
}