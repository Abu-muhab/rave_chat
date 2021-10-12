package com.abumuhab.chat.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abumuhab.chat.R
import com.abumuhab.chat.databinding.FriendTileBinding
import com.abumuhab.chat.fragments.FindFriendsFragmentDirections
import com.abumuhab.chat.models.ChatPreview
import com.abumuhab.chat.models.Friend
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class FriendAdapter :
    ListAdapter<Friend, FriendAdapter.ViewHolder>(FriendDiffCallback()) {
    class ViewHolder(private val binding: FriendTileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Friend) {
            binding.friend = item
            binding.previewContainer.setOnClickListener {
                val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val jsonAdapter: JsonAdapter<Friend> = moshi.adapter(Friend::class.java)
                it.findNavController().navigate(
                    FindFriendsFragmentDirections.actionFindFriendsFragmentToChatFragment(
                        jsonAdapter.toJson(item)
                    )
                )
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FriendTileBinding.inflate(layoutInflater, parent, false)
        return FriendAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendAdapter.ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class FriendDiffCallback : DiffUtil.ItemCallback<Friend>() {
    override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
        return oldItem.displayName == newItem.displayName && oldItem.userName == newItem.userName
    }

    override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
        return oldItem == newItem
    }
}