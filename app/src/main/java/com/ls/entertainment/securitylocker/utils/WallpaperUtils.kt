package com.ls.entertainment.securitylocker.utils

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import com.ls.entertainment.securitylocker.App
import java.io.IOException

object WallpaperUtils {

	private val UNSUPPORTED_INCREMENTAL = listOf("V10.2.2.0.MALMIXM")

	fun setWallpaper(
		bitmap: Bitmap,
		type: WallpaperType = WallpaperType.HOME,
	): Boolean {
		val manager = WallpaperManager.getInstance(App.instance)
		var error: IOException? = null
		try {
			when (type) {
				WallpaperType.HOME -> {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						manager.setBitmap(bitmap, null, false, WallpaperManager.FLAG_SYSTEM)
					} else {
						manager.setBitmap(bitmap)
					}
				}
				WallpaperType.LOCK -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
					manager.setBitmap(bitmap, null, false, WallpaperManager.FLAG_LOCK)
				}
				else               -> {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						manager.setBitmap(
							bitmap, null, false, WallpaperManager.FLAG_SYSTEM
						)
						manager.setBitmap(
							bitmap, null, false, WallpaperManager.FLAG_LOCK
						)
					} else {
						manager.setBitmap(bitmap)
					}
				}
			}
			TrackingHelper.logEvent(AllEvents.SET_WALLPAPER_SUCCESS)
			return true
		} catch (e: IOException) {
			LogUtils.logCustomMessage(e.message.toString())
			error = e
			TrackingHelper.logEvent(AllEvents.SET_WALLPAPER_FAIL)
		}
		return false
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	fun isSupported(): Boolean {
		val manager = WallpaperManager.getInstance(App.instance)
		return manager.isWallpaperSupported && manager.isSetWallpaperAllowed
	}

	@SuppressLint("NewApi")
	fun reset(): Boolean {
		try {
			WallpaperManager.getInstance(App.instance).clearWallpaper()
			return true
		} catch (e: IOException) {

		}
		return false
	}

	enum class WallpaperType {
		HOME, LOCK, BOTH, LOCK_APP;

		companion object {
			fun init(index: Int): WallpaperType {
				return when (index) {
					2    -> LOCK
					3    -> BOTH
					else -> HOME
				}
			}
		}
	}

	enum class FileTypeURL {
		PATH, URI
	}
}

