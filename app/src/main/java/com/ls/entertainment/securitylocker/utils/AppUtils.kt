package com.ls.entertainment.securitylocker.utils

import android.app.ActivityManager
import android.app.Service
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.adapter.ThemeAdapter
import com.ls.entertainment.securitylocker.adapter.WallpaperModel
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile
import java.text.DecimalFormat
import java.util.regex.Pattern


object AppUtils {
	fun isUserApp(applicationInfo: ApplicationInfo): Boolean {
		return applicationInfo.flags and 129 == 0
	}

	fun isSystemPackage(applicationInfo: ApplicationInfo): Boolean {
		return applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
	}

	fun goToMarket(packageName: String, context: Context) {
		val uri = Uri.parse(Constant.BASE_URL_STORE + packageName)
		val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
		try {
			context.startActivity(myAppLinkToMarket)
		} catch (e: ActivityNotFoundException) {
			Toast.makeText(context, context.getString(R.string.cannot_find_app), Toast.LENGTH_LONG)
				.show()
		}
	}

	fun shareApp(packageName: String, context: Context) {
		val intent = Intent()
		intent.action = Intent.ACTION_SEND
		intent.putExtra(
			Intent.EXTRA_SUBJECT,
			context.getString(R.string.share) + context.getString(R.string.app_name)
		)
		intent.putExtra(
			Intent.EXTRA_TEXT,
			context.getString(R.string.app_name) + context.getString(R.string.availble) + Constant.BASE_URL_STORE + packageName
		)
		intent.type = "text/plain"
		context.startActivity(intent)
	}

	fun sendFeedBack(context: Context) {
		val email = Intent(Intent.ACTION_SEND)
		email.putExtra(Intent.EXTRA_EMAIL, arrayOf(Constant.MAIL_FEEDBACK))
		email.putExtra(
			Intent.EXTRA_SUBJECT,
			context.getString(R.string.subject_mail_fb) + context.getString(R.string.app_name)
		)
		email.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.temp_message_fb))
		//need this to prompts email client only
		email.type = "message/rfc822"
		context.startActivity(Intent.createChooser(email, context.getString(R.string.choose_email)))
	}

	fun getListImageFromConfig(): List<WallpaperModel> {
		val list = mutableListOf<WallpaperModel>()
		RemoteConfig.commonConfig.listImage.split(AppConstant.SEPARATE_LIST_IMAGE).forEach {
			val wallpaperModel = WallpaperModel(System.currentTimeMillis().toInt(), "", it, false)
			list.add(wallpaperModel)
		}
		list.shuffle()
		return list
	}

	fun getTotalRam(): Long {
		var j: Long
		DecimalFormat("#.##")
		try {
			val randomAccessFile = RandomAccessFile("/proc/meminfo", "r")
			val matcher = Pattern.compile("(\\d+)").matcher(randomAccessFile.readLine())
			var str: String? = ""
			while (matcher.find()) {
				str = matcher.group(1)
			}
			randomAccessFile.close()
			j = Integer.valueOf(str).toInt().toLong()
		} catch (e: IOException) {
			e.printStackTrace()
			j = 0
		}
		return j * 1024
	}

	fun getAvailableRam(context: Context): Long {
		val memoryInfo = ActivityManager.MemoryInfo()
		val activityManager = context.getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
		activityManager.getMemoryInfo(memoryInfo)
		return memoryInfo.availMem
	}

	fun formatSize(j: Long): String {
		if (j <= 0) {
			return ""
		}
		val d = j.toDouble()
		val log10 = (Math.log10(d) / Math.log10(1024.0)).toInt()
		return DecimalFormat("#,##0.#").format(
			d / Math.pow(
				1024.0, log10.toDouble()
			)
		) + " " + arrayOf("B", "KB", "MB", "GB", "TB")[log10]
	}

	fun goDetailInformationApp(ctx: Context, packageName: String) {
		val intent = Intent()
		intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
		intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
		intent.data = Uri.parse(
			"package:$packageName"
		)
		ctx.startActivity(intent)
	}

	fun setWallpaper(bitmap: Bitmap?): Boolean {
		return try {
			val manager = WallpaperManager.getInstance(App.instance)
			manager.setBitmap(bitmap)
			Timber.d("Wallpaper set successfully!")
			return true
		} catch (e: IOException) {
			Timber.d("Error set wallpaper because %s", e.message)
			false
		}
	}

	fun removeNativeItem(list: MutableList<WallpaperModel>): MutableList<WallpaperModel> {
		val result = mutableListOf<WallpaperModel>()
		list.forEach {
			if (it.name != ThemeAdapter.NAME_NATIVE_ADS) {
				result.add(it)
			}
		}
		return result
	}

	fun readPolicyFromAsset(fileName: String): String {
		var string = ""
		try {
			val inputStream: InputStream = App.instance.applicationContext.assets.open(fileName)
			val size = inputStream.available()
			val buffer = ByteArray(size)
			inputStream.read(buffer)
			string = String(buffer)
		} catch (e: IOException) {
			e.printStackTrace()
		}
		return string
	}
}