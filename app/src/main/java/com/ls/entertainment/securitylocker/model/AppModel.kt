package com.ls.entertainment.securitylocker.model

import android.graphics.drawable.Drawable


data class AppModel(
    val name: String,
    val resIcon: Drawable?,
    val packageName: String,
    var isLock: Boolean
)