package com.ls.entertainment.securitylocker

import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import com.ls.entertainment.securitylocker.App.Companion.isShowLock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LockService : Service() {

	private var iconNotification: Bitmap? = null
	private var notification: Notification? = null
	var mNotificationManager: NotificationManager? = null
	private val mNotificationId = 123
	lateinit var usageStageManager: UsageStatsManager
	private var currentPackage = ""
	val packageLock = "com.bluesky.best_ringtone.free2017"

	private val receiver = object : BroadcastReceiver() {
		override fun onReceive(p0: Context?, p1: Intent?) {
			Toast.makeText(p0, "onReceive action = ${p1?.action}", Toast.LENGTH_LONG).show()
		}

	}

	override fun onBind(p0: Intent?): IBinder? {
		return null
	}

	override fun onCreate() {
		super.onCreate()
		usageStageManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
		val intentFilter = IntentFilter(Intent.ACTION_POWER_CONNECTED)
		intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
		registerReceiver(receiver, intentFilter)
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		generateForegroundNotification()
		scheduleCheck()
		return START_STICKY
	}

	private fun scheduleCheck() {
		CoroutineScope(Dispatchers.Default).launch {
			while (true) {
				delay(1000)
				val endTime = System.currentTimeMillis()
				val startTime = endTime - 10000
				var result = ""
				val event = UsageEvents.Event()
				val usageEvents = usageStageManager.queryEvents(startTime, endTime)
				while (usageEvents.hasNextEvent()) {
					usageEvents.getNextEvent(event)
					if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
						result = event.packageName
						if (result != currentPackage) {
							currentPackage = result
							if (currentPackage == packageLock) {
								startActivity(
									Intent(
										this@LockService,
										UnlockActivity::class.java
									).apply {
										addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
									})
							}
						}
					}
				}
				Log.d("Sontv", "Package: $currentPackage")
			}
		}
	}

	private fun generateForegroundNotification() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val intentMainLanding = Intent(this, MainActivity::class.java)
			val pendingIntent = PendingIntent.getActivity(
				this,
				0,
				intentMainLanding,
				PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
			)
			iconNotification = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
			if (mNotificationManager == null) {
				mNotificationManager =
					this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				assert(mNotificationManager != null)
				mNotificationManager?.createNotificationChannelGroup(
					NotificationChannelGroup("chats_group", "Chats")
				)
				val notificationChannel = NotificationChannel(
					"service_channel", "Service Notifications", NotificationManager.IMPORTANCE_MIN
				)
				notificationChannel.enableLights(false)
				notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
				mNotificationManager?.createNotificationChannel(notificationChannel)
			}
			val builder = NotificationCompat.Builder(this, "service_channel")

			builder.setContentTitle(
				StringBuilder(resources.getString(R.string.app_name)).append(" service is running")
					.toString()
			).setTicker(
				StringBuilder(resources.getString(R.string.app_name)).append("service is running")
					.toString()
			).setContentText("Touch to open") //                    , swipe down for more options.
				.setSmallIcon(R.drawable.ic_launcher_foreground)
				.setPriority(NotificationCompat.PRIORITY_HIGH).setWhen(0).setOnlyAlertOnce(true)
				.setContentIntent(pendingIntent).setOngoing(true)
			if (iconNotification != null) {
				builder.setLargeIcon(Bitmap.createScaledBitmap(iconNotification!!, 128, 128, false))
			}
			builder.color = ResourcesCompat.getColor(resources, R.color.purple_200, null)
			notification = builder.build()
			startForeground(mNotificationId, notification)
		}

	}

	override fun onDestroy() {
		super.onDestroy()
		unregisterReceiver(receiver)
	}
}