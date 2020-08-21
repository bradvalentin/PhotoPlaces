package com.example.photoplaces.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.photoplaces.R
import java.text.DecimalFormat

fun ImageView.loadImageFromUrl(url: String?) {
    val options = RequestOptions()
        .placeholder(R.drawable.no_image)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
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

fun Float.formatToKm(fracDigits: Int): String {
    val df = DecimalFormat()
    df.maximumFractionDigits = fracDigits
    return df.format(this / 1000.0).plus(" km")
}