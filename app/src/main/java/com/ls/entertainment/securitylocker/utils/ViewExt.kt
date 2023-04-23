package com.ls.entertainment.securitylocker.utils

import android.graphics.Color
import android.view.View
import java.util.Random

fun View.setOnSafeClickListener(safeTime: Long = 500, clickListener: (View?) -> Unit) {
	setOnClickListener(SafeOnClickListener.newInstance(safeTime) {
		clickListener(it)
	})
}

fun View.setRandomBackgroundColor() {
	val random = Random()
	val color = Color.argb(180, random.nextInt(256), random.nextInt(256), random.nextInt(256))
	setBackgroundColor(color)
}