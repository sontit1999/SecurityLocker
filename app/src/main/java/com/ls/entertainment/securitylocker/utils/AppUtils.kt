package com.ls.entertainment.securitylocker.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.widget.Toast
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.adapter.WallpaperModel


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
}