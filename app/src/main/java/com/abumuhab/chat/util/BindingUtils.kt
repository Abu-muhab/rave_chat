package com.abumuhab.chat.util

import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("layoutWidth")
fun ImageView.settLayoutWidth(item: Float){
    val params: ViewGroup.LayoutParams = layoutParams
    params.width = item.toInt()
    layoutParams=params
}

@BindingAdapter("layoutHeight")
fun ImageView.setLayoutHeight(item: Float){
    val params: ViewGroup.LayoutParams = layoutParams
    params.height = item.toInt()
    layoutParams=params
}