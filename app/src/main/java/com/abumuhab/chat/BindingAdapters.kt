package com.abumuhab.chat

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

@BindingAdapter("imageUrl")
fun bindImage(imageView: ImageView,imageUrl: String?){
    imageUrl?.let {
        val imageUri = imageUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imageView.context)
            .load(imageUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_baseline_person_24).error(R.drawable.ic_baseline_person_24))
            .into(imageView)
    }
}