package com.abumuhab.chat.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.marginEnd
import androidx.databinding.ViewDataBinding
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abumuhab.chat.databinding.ChatPreviewBinding
import com.abumuhab.chat.databinding.MessageCardBinding
import com.abumuhab.chat.databinding.MessageCardIncomingBinding
import com.abumuhab.chat.fragments.FriendsFragmentDirections
import com.abumuhab.chat.models.ChatPreview
import com.abumuhab.chat.models.Message
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.Exception

class ChatAdapter :
    ListAdapter<Message, ChatAdapter.ViewHolder>(MessageDiffCallback()) {

    class ViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, nextMessage: Message?) {
            if (itemViewType == 0) {
                val outgoingBinding = binding as MessageCardBinding
                outgoingBinding.content = message.content
                outgoingBinding.messageBreak =
                    !(nextMessage != null && message.sender == nextMessage.sender)
            } else {
                val incomingBinding = binding as MessageCardIncomingBinding
                incomingBinding.content = message.content
                incomingBinding.messageBreak =
                    !(nextMessage != null && message.sender == nextMessage.sender)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding = if (viewType == 0) {
            MessageCardBinding.inflate(layoutInflater, parent, false)
        } else {
            MessageCardIncomingBinding.inflate(layoutInflater, parent, false)
        }
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = getItem(position)
        var nextMessage: Message? = null
        try {
            nextMessage = getItem(position + 1)
        } catch (e: Exception) {
        }
        holder.bind(message, nextMessage)
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        if (item.sender == "abumuhab") {
            return 0;
        }
        return 1
    }
}

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.content == newItem.content
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }

}