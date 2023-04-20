package com.ls.entertainment.securitylocker.utils

import android.content.Context
import android.net.ConnectivityManager
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import com.entertainment.basemvvmproject.R
import kotlin.properties.Delegates

object AppConfig {


	lateinit var connectivityManager: ConnectivityManager
	lateinit var displayMetrics: DisplayMetrics
	lateinit var appName: String
	var statusBarSize by Delegates.notNull<Int>()

	val widthScreen: Int
		get() = displayMetrics.widthPixels

	val heightScreen: Int
		get() = displayMetrics.heightPixels

	var aspectRatio: Float = 1.78f


	fun setup(context: Context) {
		appName = context.getString(R.string.app_name)
		connectivityManager =
			context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		displayMetrics = getScreen(context)
		statusBarSize = getStatusBarHeight(context)
	}


	private fun getScreen(context: Context): DisplayMetrics {
		val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
		val dm = DisplayMetrics()
		windowManager.defaultDisplay.getRealMetrics(dm)
		return dm
	}

	private fun getStatusBarHeight(context: Context): Int {
		val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
		return if (resourceId > 0) {
			context.resources.getDimensionPixelSize(resourceId)
		} else 0
	}
}

fun dpToPx(dp: Float): Int =
	TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, AppConfig.displayMetrics).toInt()

fun dp2Px(dp: Float): Float =
	TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, AppConfig.displayMetrics)

fun pxToDp(px: Int): Float = px / AppConfig.displayMetrics.density

fun spToPx(sp: Float): Float =
	TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, AppConfig.displayMetrics)