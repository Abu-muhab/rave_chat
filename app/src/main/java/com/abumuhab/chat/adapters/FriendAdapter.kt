package com.abumuhab.chat.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abumuhab.chat.databinding.FriendTileBinding
import com.abumuhab.chat.models.Friend

class FriendAdapter:
    ListAdapter<Friend, FriendAdapter.ViewHolder>(FriendDiffCallback()) {
    class ViewHolder(private val binding: FriendTileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Friend) {
            binding.friend = item
            binding.avatar.large = true
            binding.avatar.resourceId = item.imageResource
            binding.previewContainer.setOnClickListener {

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
        return oldItem.displayName == newItem.displayName && oldItem.userName==newItem.userName
    }

    override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
        return oldItem == newItem
    }
}