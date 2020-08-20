package com.example.photoplaces.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.photoplaces.R

fun ImageView.loadImageFromUrl(url: String?) {
    val options = RequestOptions()
        .placeholder(R.drawable.no_image)
        .error(R.drawable.no_image)

    Glide.with(context)
        .setDefaultRequestOptions(options)
        .load(url)
        .into(this)
}

@BindingAdapter("android:imageUrl")
fun loadImage(view: ImageView, url: String?) {
    view.loadImageFromUrl(url)
}