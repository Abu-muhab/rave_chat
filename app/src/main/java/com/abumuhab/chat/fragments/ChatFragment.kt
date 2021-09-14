package com.abumuhab.chat.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.abumuhab.chat.R
import com.abumuhab.chat.adapters.ChatAdapter
import com.abumuhab.chat.databinding.FragmentChatBinding
import com.abumuhab.chat.models.ChatPreview
import com.abumuhab.chat.models.Message
import com.abumuhab.chat.network.AuthPayload
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ChatFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentChatBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_chat, container, false)
        val args = ChatFragmentArgs.fromBundle(requireArguments())

        val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter: JsonAdapter<ChatPreview> = moshi.adapter(ChatPreview::class.java)

        val preview: ChatPreview? = jsonAdapter.fromJson(args.chatPreview)

        binding.preview = preview

        val messages = arrayOf(
            Message(
                "hello",
                "12:30",
                "abumuhab"
            ),
            Message(
                "Hii",
                "12:31",
                preview?.name!!
            ),
            Message(
                "i was wondering if we could work on soemthing",
                "12:30",
                "abumuhab"
            ),
            Message(
                "i have this mad idea",
                "12:30",
                "abumuhab"
            ),
            Message(
                "Let's hear it. i am excited about this my gee!!",
                "12:30",
                preview.name
            ),
            Message(
                "I need something to work on",
                "12:30",
                preview.name
            ),
            Message(
                "this should do it",
                "12:30",
                preview.name
            ),
            Message(
                "I need something to work on. I need something to work on. I need something to work on. I need something to work on. I need something to work on",
                "12:30",
                preview.name
            ),
        )

        val adapter = ChatAdapter()
        adapter.submitList(messages.toList())

        binding.messageList.adapter = adapter

        binding.lifecycleOwner = this

        return binding.root
    }
}