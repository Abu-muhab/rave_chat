package com.abumuhab.chat.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abumuhab.chat.databinding.ChatPreviewBinding
import com.abumuhab.chat.fragments.FriendsFragmentDirections
import com.abumuhab.chat.models.ChatPreview
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class FriendAdapter :
    ListAdapter<ChatPreview, FriendAdapter.ViewHolder>(ChatPreviewDiffCallback()) {

    class ViewHolder(private val binding: ChatPreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatPreview) {
            binding.preview = item
            binding.avatar.large = true
            binding.avatar.resourceId = item.imageResource
            binding.previewContainer.setOnClickListener {
                val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val jsonAdapter: JsonAdapter<ChatPreview> = moshi.adapter(ChatPreview::class.java)
                it.findNavController()
                    .navigate(FriendsFragmentDirections.actionFriendsFragmentToChatFragment(jsonAdapter.toJson(item)))
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
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: ChatPreview, newItem: ChatPreview): Boolean {
        return oldItem == newItem
    }
}