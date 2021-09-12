package com.abumuhab.chat.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.abumuhab.chat.R
import com.abumuhab.chat.databinding.AvatarBinding
import com.abumuhab.chat.databinding.FragmentFriendsBinding

class FriendsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentFriendsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_friends, container, false)
        (activity as AppCompatActivity).supportActionBar?.hide()

        val avatars = arrayOf(
            R.drawable.avatar_1,
            R.drawable.avatar_16,
            R.drawable.avatar_22,
            R.drawable.avatar_31,
            R.drawable.avatar_33,
            R.drawable.avatar_1,
            R.drawable.avatar_4,
            R.drawable.avatar_5,
            R.drawable.avatar_6,
        )

        avatars.forEach {
            val avatarBinding = AvatarBinding.inflate(inflater, binding.friendsPreviewLayout, false)
            avatarBinding.resourceId = it
            binding.lifecycleOwner = this
            binding.friendsPreviewLayout.addView(avatarBinding.root)
        }
        return binding.root
    }
}