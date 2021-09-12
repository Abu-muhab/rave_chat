package com.abumuhab.chat.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.abumuhab.chat.R
import com.abumuhab.chat.databinding.FragmentChatBinding
import com.abumuhab.chat.models.ChatPreview
import com.abumuhab.chat.network.AuthPayload
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ChatFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentChatBinding = DataBindingUtil.inflate(layoutInflater,R.layout.fragment_chat,container,false)
        val args = ChatFragmentArgs.fromBundle(requireArguments())

        val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter: JsonAdapter<ChatPreview> = moshi.adapter(ChatPreview::class.java)

        val preview: ChatPreview? = jsonAdapter.fromJson(args.chatPreview)

        binding.preview = preview

        return  binding.root
    }
}