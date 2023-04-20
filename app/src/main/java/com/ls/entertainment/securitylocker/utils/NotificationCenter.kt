/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ls.entertainment.securitylocker.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.bumptech.glide.request.RequestOptions
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.BuildConfig
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.ui.splash.SplashActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


object NotificationCenter {

	const val ACTION_NOTIFICATION = BuildConfig.APPLICATION_ID + ".notification"
	const val CHANNEL_ID = BuildConfig.APPLICATION_ID
	const val CHANNEL_ID_REMIND_OPEN = BuildConfig.APPLICATION_ID + ".openApp"
	const val GROUP_KEY_REMIND_OPEN_APP = BuildConfig.APPLICATION_ID + ".offline"
	const val GROUP_KEY_FCM = BuildConfig.APPLICATION_ID + ".fcm"
	const val CHANNEL_DESCRIPTION_FCM = "Weekly events"
	const val CHANNEL_DESCRIPTION_REMIND_OPEN = "Remind open application"
	const val EXTRA_TAG = "NOTIFICATION"
	const val MESSAGE = "message"
	const val TITLE = "title"
	const val ID = "ID"
	const val DATA = "DATA"
	const val ACTION = "ACTION"
	const val SUMMARY_TEXT = "SUMMARY_TEXT"
	const val BODY = "body"
	const val IMAGE = "image"

	const val SUMMARY_ID_FCM = 0
	const val SUMMARY_ID_REMIND_OPEN_APP = 1

	const val TAG_NOTIFY_FCM = "fcm"
	const val TAG_NOTIFY_OFFLINE = "offline"
	const val KEY_TAG_NOTIFY = "KEY_TAG_NOTIFY"

	@JvmStatic
	fun push(
		bundle: Bundle,
		bigPicture: Bitmap? = null,
		image: String? = null,
		isFromFCM: Boolean = false
	): Boolean {
		val cal = Calendar.getInstance()
		val hour = cal.get(Calendar.HOUR_OF_DAY)
		val app = App.instance
		val con = app.applicationContext ?: return false

		val notificationId = System.currentTimeMillis().toInt()
		bundle.putString(ID, notificationId.toString() + "")

		val title = bundle.getString(TITLE)
		val message = bundle.getString(MESSAGE)
		val intent = Intent()
		intent.action = ACTION_NOTIFICATION
		intent.setClass(con, SplashActivity::class.java)
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
		if (isFromFCM) {
			intent.putExtra(KEY_TAG_NOTIFY, TAG_NOTIFY_FCM)
			TrackingHelper.logEvent(AllEvents.E1_NOTIFICATION_FCM_RECEIVE)
		} else {
			intent.putExtra(KEY_TAG_NOTIFY, TAG_NOTIFY_OFFLINE)
			TrackingHelper.logEvent(AllEvents.E1_NOTIFICATION_OFFLINE_RECEIVE)
		}
		intent.putExtra(EXTRA_TAG, bundle)
		val pendingIntent = PendingIntent.getActivity(
			con,
			notificationId,
			intent,
			PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
		)
		val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
		val channelId = if (isFromFCM) CHANNEL_ID else CHANNEL_ID_REMIND_OPEN
		val builder = NotificationCompat.Builder(con, channelId).setSmallIcon(R.mipmap.ic_launcher)
			.setTicker(title).setContentTitle(title).setContentText(message).setAutoCancel(true)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT).setSound(sound)
			.setContentIntent(pendingIntent).setGroup(getGroupKey(isFromFCM))
		CoroutineScope(Dispatchers.IO).launch {
			val bitmap = bigPicture ?: image?.let {
				try {
					val url = image
					GlideHelper.getBitmap(
						url,
						RequestOptions().override(AppConfig.widthScreen, AppConfig.heightScreen)
					)
				} catch (e: Exception) {
					null
				}
			}
			if (bitmap != null) {
				val bigStyle =
					NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null)
				bundle.getString(SUMMARY_TEXT)?.also { bigStyle.setSummaryText(it) }
				builder.setLargeIcon(bitmap).setStyle(bigStyle)
			} else {
				builder.setLargeIcon(
					BitmapFactory.decodeResource(
						con.resources, R.mipmap.ic_launcher
					)
				).setStyle(NotificationCompat.BigTextStyle().bigText(message))
			}

			val needCreateSummaryNotification = Build.VERSION.SDK_INT < Build.VERSION_CODES.N
			val manager = con.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
			manager.notify(notificationId, builder.build())
			if (needCreateSummaryNotification) {
				val summaryNotification =
					NotificationCompat.Builder(con, channelId).setContentTitle(title)
						//set content text to support devices running API level < 24
						.setContentText(message).setSmallIcon(R.mipmap.ic_launcher_round)
						.setStyle(NotificationCompat.InboxStyle()).setGroup(getGroupKey(isFromFCM))
						//set this notification as the summary for the group
						.setGroupSummary(true).build()
				manager.notify(getSummaryID(isFromFCM), summaryNotification)
			}
		}
		return true
	}

	private fun getSummaryID(fromFCM: Boolean) =
		if (fromFCM) SUMMARY_ID_FCM else SUMMARY_ID_REMIND_OPEN_APP

	private fun getGroupKey(isFromFCM: Boolean) =
		if (isFromFCM) GROUP_KEY_FCM else GROUP_KEY_REMIND_OPEN_APP

	fun createChannelNotification() {
		try {
			val con = App.instance.applicationContext ?: return
			val manager = con.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
			createNotificationChannel(manager, CHANNEL_ID, CHANNEL_DESCRIPTION_FCM)
			createNotificationChannel(
				manager, CHANNEL_ID_REMIND_OPEN, CHANNEL_DESCRIPTION_REMIND_OPEN
			)
		} catch (e: Exception) {
			LogUtils.logCustomMessage(e.message.toString())
		}
	}

	private fun createNotificationChannel(
		manager: NotificationManager, channelId: String, channelDescription: String
	) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val importance = NotificationManager.IMPORTANCE_DEFAULT
			val notificationChannel = NotificationChannel(channelId, channelDescription, importance)
			notificationChannel.description = channelDescription
			manager.createNotificationChannel(notificationChannel)
		}
	}
}