package com.example.photoplaces.utils

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.photoplaces.R
import com.google.android.material.textfield.TextInputLayout
import java.text.DecimalFormat

const val FADE_ANIMATION_DURATION = 500
const val BLOCK_IN_MILLIS = 500

fun ImageView.loadImageFromUrl(url: String?) {
    val options = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .error(R.drawable.no_image)


    Glide.with(context)
        .setDefaultRequestOptions(options)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade(FADE_ANIMATION_DURATION))
        .into(this)
}

@BindingAdapter("android:imageUrl")
fun loadImage(view: ImageView, url: String?) {
    view.loadImageFromUrl(url)
}

@BindingAdapter("android:onSingleClick")
fun View.safeClick(listener: View.OnClickListener) {
    var lastClickTime: Long = 0
    this.setOnClickListener {
        if (SystemClock.elapsedRealtime() - lastClickTime < BLOCK_IN_MILLIS) return@setOnClickListener
        lastClickTime = SystemClock.elapsedRealtime()
        listener.onClick(this)
    }
}

@BindingAdapter("isValid", "errorText")
fun setErrorMessage(target: TextInputLayout, isValid: Boolean, errorText: String) {
    if (isValid) target.error = null
    else target.error = errorText
}

inline fun <T1: Any, T2: Any, T3: Any, T4: Any, R: Any> safeLet(p1: T1?, p2: T2?, p3: T3?, p4: T4?, block: (T1, T2, T3, T4)->R?): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null) block(p1, p2, p3, p4) else null
}

fun Float.formatToKm(fracDigits: Int): String {
    val df = DecimalFormat()
    df.maximumFractionDigits = fracDigits
    return df.format(this / 1000.0).plus(" km")
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}
