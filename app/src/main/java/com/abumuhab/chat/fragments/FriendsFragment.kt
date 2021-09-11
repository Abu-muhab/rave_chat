package com.abumuhab.chat.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.abumuhab.chat.R
import com.abumuhab.chat.databinding.FragmentFriendsBinding

class FriendsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentFriendsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_friends, container, false)
        (activity as AppCompatActivity).supportActionBar?.hide()
        return binding.root
    }
}