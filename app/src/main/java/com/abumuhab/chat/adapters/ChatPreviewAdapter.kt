package com.abumuhab.chat.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abumuhab.chat.databinding.ChatPreviewBinding
import com.abumuhab.chat.fragments.ChatHistoryFragmentDirections
import com.abumuhab.chat.models.ChatPreview
import com.abumuhab.chat.models.Friend
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ChatPreviewAdapter :
    ListAdapter<ChatPreview, ChatPreviewAdapter.ViewHolder>(ChatPreviewDiffCallback()) {

    class ViewHolder(private val binding: ChatPreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatPreview) {
            binding.preview = item
            binding.previewContainer.setOnClickListener {
                val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val jsonAdapter: JsonAdapter<Friend> = moshi.adapter(Friend::class.java)
                it.findNavController()
                    .navigate(
                        ChatHistoryFragmentDirections.actionChatHistoryFragmentToChatFragment(
                            jsonAdapter.toJson(item.friend)
                        )
                    )
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ChatPreviewBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class ChatPreviewDiffCallback : DiffUtil.ItemCallback<ChatPreview>() {
    override fun areItemsTheSame(oldItem: ChatPreview, newItem: ChatPreview): Boolean {
        return oldItem.friend.userName == newItem.friend.userName
    }

    override fun areContentsTheSame(oldItem: ChatPreview, newItem: ChatPreview): Boolean {
        return oldItem == newItem
    }
}