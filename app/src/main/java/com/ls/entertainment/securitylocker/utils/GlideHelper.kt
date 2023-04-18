package com.ls.entertainment.securitylocker.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.NoTransition
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.request.transition.Transition.ViewAdapter
import com.bumptech.glide.request.transition.TransitionFactory
import com.ls.entertainment.securitylocker.App
import java.io.File

object GlideHelper {
    var totalTimeLoadMinThumb = 0L
    var loadMinThumbCount = 0

    fun load(image: ImageView, url: String?) {

        val options = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .dontTransform()

        Glide.with(image).load(url)
            .apply(options)
            .into(image)
    }

    fun load(image: ImageView, res: Int) {

        val options = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .dontTransform()

        Glide.with(image).load(res)
            .apply(options)
            .into(image)
    }


    fun download(url: String, options: RequestOptions? = null): FutureTarget<File> {
        var requestOptions = options ?: RequestOptions()
            .override(Target.SIZE_ORIGINAL)
        requestOptions = requestOptions.priority(Priority.HIGH)
        val fail = Glide.with(App.instance).downloadOnly().load(url)
        return Glide.with(App.instance)
            .downloadOnly()
            .load(url)
            .error(fail)
            .apply(requestOptions)
            .submit()
    }

    fun getBitmap(url: String, options: RequestOptions? = null): Bitmap {
        var requestOptions = options ?: RequestOptions()
            .override(Target.SIZE_ORIGINAL)
        requestOptions = requestOptions.priority(Priority.HIGH)
        val fail = Glide.with(App.instance).asBitmap().load(url)
        return Glide.with(App.instance).asBitmap().load(url).error(fail).apply(requestOptions)
            .submit().get()
    }

    fun clear(view: ImageView) {
        try {
            Glide.with(view).clear(view)
            view.setImageDrawable(null)
        } catch (e: IllegalArgumentException) {
        }
    }

    // run in background
    fun clearDiskCache() {
        Glide.get(App.instance).clearDiskCache()
    }

    fun clearMemory() {
        Glide.get(App.instance).clearMemory()
    }

    val TRANSITION = Transition { current: Drawable?, adapter: ViewAdapter ->
        if (adapter.view is ImageView) {
            val image = adapter.view as ImageView
            if (image.drawable == null) {
                image.alpha = 0f
                image.animate().alpha(1f)
            }
        }
        false
    }

    val TRANSITION_FACTORY =
        TransitionFactory { dataSource: DataSource, isFirstResource -> if (dataSource == DataSource.REMOTE) TRANSITION else NoTransition.get() }

}