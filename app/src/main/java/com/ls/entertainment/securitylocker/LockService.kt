package com.ls.entertainment.securitylocker

import android.app.*
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.SortedMap
import java.util.TreeMap

class LockService : Service() {
	private var wakeLock: PowerManager.WakeLock? = null
	private var iconNotification: Bitmap? = null
	private var notification: Notification? = null
	var mNotificationManager: NotificationManager? = null
	private val mNotificationId = 123
	lateinit var usageStageManager: UsageStatsManager
	private var currentPackage = ""
	lateinit var editor : SharedPreferences.Editor
	val packageLock = "com.bluesky.best_ringtone.free2017"
	private var currentApp : String? = null
	lateinit var sharedPreference : SharedPreferences

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

	@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		wakeUp()
		generateForegroundNotification()
		scheduleCheck()
		return START_STICKY
	}

	private fun wakeUp() {
		wakeLock =
			(getSystemService(Context.POWER_SERVICE) as PowerManager).run {
				newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Lock::lock").apply {
					acquire()
				}
			}
	}


	@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
	private fun scheduleCheck() {
		CoroutineScope(Dispatchers.Default).launch {
			while (true) {
				delay(1000)
				currentApp = null
				val usm = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
				val time = System.currentTimeMillis()
				val applist =
					usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time)
				if (applist != null && applist.size > 0) {
					val mySortedMap: SortedMap<Long, UsageStats> = TreeMap()
					for (usageStats in applist) {
						mySortedMap[usageStats.lastTimeUsed] = usageStats
					}
					if (!mySortedMap.isEmpty()) {
						currentApp = mySortedMap[mySortedMap.lastKey()]!!.packageName
					}
				}
				Log.d("Sontv", "Current App in foreground is: $currentApp")
				if (currentApp != null ) {
					checkMyApp(currentApp!!)
				}

			}
		}
	}

	//  && appLocked!!.contains("com.nvd.applocker") com.sec.android.app.launcher
	private fun checkMyApp(currentApp : String) : Unit{
		sharedPreference =  getSharedPreferences("AppLock", Context.MODE_PRIVATE)
		editor = sharedPreference.edit()

		//val appLocked = sharedPreference.getString(currentApp, null)
		val appLocked = "com.ls.entertainment.documentviewer"
		if (appLocked != null)
			Log.d("ccc", appLocked!!)

		val getLastApp = sharedPreference.getString("mLastApp", null)
		if (currentApp.lowercase(Locale.getDefault()).contains("launcher") && getLastApp != null){
			editor.putString("mLastApp", null).apply()
		}
		//com.android.launcher3
		if ((getLastApp != appLocked && appLocked != null)){
			if (appLocked != "com.ls.entertainment.securitylocker")
				editor.putString("mLastApp", appLocked).apply()

			// mở màn hình khóa
			val intent = Intent(this, UnlockActivity::class.java)
			intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
			startActivity(intent)
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

	override fun onTaskRemoved(rootIntent: Intent?) {
		super.onTaskRemoved(rootIntent)
		val restartServiceIntent = Intent(applicationContext, LockService::class.java).also {
			it.setPackage(packageName)
		};
		val restartServicePendingIntent: PendingIntent = PendingIntent.getService(this, 1, restartServiceIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT)
		val alarmService: AlarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
		alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePendingIntent)
	}
}