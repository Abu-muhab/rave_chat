package com.abumuhab.chat.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abumuhab.chat.databinding.MessageCardBinding
import com.abumuhab.chat.databinding.MessageCardIncomingBinding
import com.abumuhab.chat.models.Message
import com.abumuhab.chat.models.UserData
import java.lang.Exception

class ChatAdapter(var userData: UserData?) :
    ListAdapter<Message, ChatAdapter.ViewHolder>(MessageDiffCallback()) {

    class ViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, nextMessage: Message?) {
            if (itemViewType == 0) {
                val outgoingBinding = binding as MessageCardBinding
                outgoingBinding.content = message.content
                if (nextMessage == null) {
                    outgoingBinding.messageBreak = false
                } else {
                    if (message.from != nextMessage.from) {
                        outgoingBinding.messageBreak = true
                    }
                }
            } else {
                val incomingBinding = binding as MessageCardIncomingBinding
                incomingBinding.content = message.content
                incomingBinding.messageBreak = false
                if (nextMessage == null) {
                    incomingBinding.messageBreak = false
                } else {
                    if (message.from != nextMessage.from) {
                        incomingBinding.messageBreak = true
                    }
                }
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
        if (item.from == userData!!.user.userName) {
            return 0
        }
        return 1
    }
}

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.dbId == newItem.dbId
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}