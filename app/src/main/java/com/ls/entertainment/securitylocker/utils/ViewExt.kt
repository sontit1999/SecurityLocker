package com.ls.entertainment.securitylocker.utils

import android.view.View

fun View.setOnSafeClickListener(safeTime: Long = 500, clickListener: (View?) -> Unit) {
	setOnClickListener(SafeOnClickListener.newInstance(safeTime) {
		clickListener(it)
	})
}