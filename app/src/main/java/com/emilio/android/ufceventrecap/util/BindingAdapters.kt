
package com.emilio.android.ufceventrecap.util

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

/**
 * Binding adapter used to hide the spinner once data is available.
 */
@BindingAdapter("isNetworkError", "playlist")
fun hideIfNetworkError(view: View, isNetWorkError: Boolean, playlist: Any?) {
    view.visibility = if (playlist != null) View.GONE else View.VISIBLE

    if(isNetWorkError) {
        view.visibility = View.GONE
    }
}

/**
 * Binding adapter used to display images from URL using Glide
 */
@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: String?) {
    if (!url.isNullOrBlank())
        Glide.with(imageView.context).load(url).into(imageView)
    else
        Glide.with(imageView.context).load("https://www.thesportsdb.com/images/media/league/fanart/qtrutt1448323197.jpg").into(imageView)
}