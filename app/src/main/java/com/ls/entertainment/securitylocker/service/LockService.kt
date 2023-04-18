package com.ls.entertainment.securitylocker.service

import android.app.*
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.ls.entertainment.securitylocker.MainActivity
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.UnlockActivity
import com.ls.entertainment.securitylocker.utils.AlarmUtils
import com.ls.entertainment.securitylocker.utils.SharePreferenceUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class LockService : Service() {
	private var wakeLock: PowerManager.WakeLock? = null
	private var iconNotification: Bitmap? = null
	private var notification: Notification? = null
	var mNotificationManager: NotificationManager? = null
	private val mNotificationId = 123
	lateinit var usageStageManager: UsageStatsManager
	lateinit var editor: SharedPreferences.Editor
	private var currentPackageName: String? = null
	lateinit var sharedPreference: SharedPreferences
	val packageAppLockTest = "com.ls.entertainment.documentviewer"

	private val receiver = object : BroadcastReceiver() {
		override fun onReceive(p0: Context?, p1: Intent?) {

		}

	}

	override fun onBind(p0: Intent?): IBinder? {
		return null
	}

	@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
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
		wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
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
				currentPackageName = null
				val usm = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
				val time = System.currentTimeMillis()
				val listAppRecent =
					usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time)
				if (listAppRecent != null && listAppRecent.size > 0) {
					val mySortedMap: SortedMap<Long, UsageStats> = TreeMap()
					for (usageStats in listAppRecent) {
						mySortedMap[usageStats.lastTimeUsed] = usageStats
					}
					if (!mySortedMap.isEmpty()) {
						currentPackageName = mySortedMap[mySortedMap.lastKey()]!!.packageName
					}
				}
				Log.d("Sontv", "Current App in foreground is: $currentPackageName")
				currentPackageName?.let {
					checkNeedLockApp(it)
				}

			}
		}
	}

	private fun checkNeedLockApp(currentApp: String) {
		//val appLocked = sharedPreference.getString(currentApp, null)
		val listAppNeedLock = SharePreferenceUtils.getInstance().getListPackageLock()
		Log.d("Sontv", "Size list lock = " + listAppNeedLock.size)
		val appLocked = if (listAppNeedLock.contains(currentApp)) currentApp else null
		val lastPackageLock = SharePreferenceUtils.getInstance().lastPackageLock
		if (currentApp.lowercase(Locale.getDefault())
				.contains("launcher") && lastPackageLock != null
		) {
			SharePreferenceUtils.getInstance().lastPackageLock = null
		}
		Log.d("Sontv", "AppLocked = $appLocked, lastPackageLock = $lastPackageLock")
		if ((lastPackageLock != appLocked && appLocked != null)) {
			if (appLocked != packageName) {
				SharePreferenceUtils.getInstance().lastPackageLock = appLocked
			}
			openLockActivity()
		}
	}

	private fun openLockActivity() {
		val intent = Intent(this, UnlockActivity::class.java)
		intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
		startActivity(intent)
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
				StringBuilder(resources.getString(R.string.app_name)).append(" secure by Security Locker")
					.toString()
			)
				.setSmallIcon(R.drawable.baseline_lock_24)
				.setPriority(NotificationCompat.PRIORITY_HIGH).setWhen(0).setOnlyAlertOnce(true)
				.setContentIntent(pendingIntent).setOngoing(true)
			if (iconNotification != null) {
				builder.setLargeIcon(Bitmap.createScaledBitmap(iconNotification!!, 128, 128, false))
			}
			notification = builder.build()
			startForeground(mNotificationId, notification)
		}

	}

	@RequiresApi(Build.VERSION_CODES.M)
	override fun onDestroy() {
		super.onDestroy()
		unregisterReceiver(receiver)
		AlarmUtils.setAlarm(this, AlarmUtils.ACTION_REPEAT_SERVICE, 1000)
	}

	@RequiresApi(Build.VERSION_CODES.M)
	override fun onTaskRemoved(rootIntent: Intent?) {
		super.onTaskRemoved(rootIntent)
		Log.d("Sontv", "onTaskRemoved")
		AlarmUtils.setAlarm(this, AlarmUtils.ACTION_REPEAT_SERVICE, 1000)
	}
}