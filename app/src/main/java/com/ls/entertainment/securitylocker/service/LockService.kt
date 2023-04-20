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
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.ls.entertainment.securitylocker.MainActivity
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.UnlockActivity
import com.ls.entertainment.securitylocker.utils.AlarmUtils
import com.ls.entertainment.securitylocker.utils.AppConstant.CHANEL_GROUP_ID
import com.ls.entertainment.securitylocker.utils.AppConstant.CHANEL_GROUP_NAME
import com.ls.entertainment.securitylocker.utils.AppConstant.CHANEL_ID
import com.ls.entertainment.securitylocker.utils.AppConstant.CHANEL_NAME
import com.ls.entertainment.securitylocker.utils.LogUtils
import com.ls.entertainment.securitylocker.utils.SharePreferenceUtils
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class LockService : Service() {

	private var wakeLock: PowerManager.WakeLock? = null
	private var iconNotification: Bitmap? = null
	private var notification: Notification? = null
	private val mNotificationId = 123
	private var currentPackageName: String? = null
	lateinit var usageStageManager: UsageStatsManager
	var mNotificationManager: NotificationManager? = null
	private var isServiceStarted = false


	override fun onBind(p0: Intent?): IBinder? {
		return null
	}

	@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
	override fun onCreate() {
		super.onCreate()
		usageStageManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
		generateForegroundNotification()
	}

	@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		wakeUp()
		scheduleCheck()
		return START_STICKY
	}

	@OptIn(DelicateCoroutinesApi::class)
	private fun wakeUp() {

		if (isServiceStarted) return
		isServiceStarted = true

		wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
			newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Lock::lock").apply {
				acquire(10 * 60 * 1000L /*10 minutes*/)
			}
		}

		// we need this lock so our service gets not affected by Doze Mode
		wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
			newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Lock::lock").apply {
				acquire()
			}
		}

		// we're starting a loop in a coroutine
		GlobalScope.launch(Dispatchers.IO) {
			while (isServiceStarted) {
				launch(Dispatchers.IO) {
					pingFakeServer()
				}
				delay(30000)
			}
		}
	}

	private fun pingFakeServer() {
		val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmmZ")
		val gmtTime = df.format(Date())

		val deviceId = Settings.Secure.getString(
			applicationContext.contentResolver,
			Settings.Secure.ANDROID_ID
		)

		val json = """
                {
                    "deviceId": "$deviceId",
                    "createdAt": "$gmtTime"
                }
            """
		try {
			Fuel.post("https://jsonplaceholder.typicode.com/posts").jsonBody(json)
				.response { _, _, result ->
					val (bytes, error) = result
					if (bytes != null) {
						LogUtils.logCustomMessage("[response bytes] ${String(bytes)}")
					} else {
						LogUtils.logCustomMessage("[response error] ${error?.message}")
					}
				}
		} catch (e: Exception) {
			LogUtils.logCustomMessage("Error making the request: ${e.message}")
		}
	}


	@OptIn(DelicateCoroutinesApi::class)
	@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
	private fun scheduleCheck() {
		GlobalScope.launch(Dispatchers.IO) {
			while (true) {
				delay(500)
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
				currentPackageName?.let {
					checkNeedLockApp(it)
				}

			}
		}
	}

	private fun checkNeedLockApp(currentApp: String) {
		val listAppNeedLock = SharePreferenceUtils.getInstance().getListPackageLock()
		val appLocked = if (listAppNeedLock.contains(currentApp)) currentApp else null
		val lastPackageLock = SharePreferenceUtils.getInstance().lastPackageLock
		if (currentApp.lowercase(Locale.getDefault())
				.contains("launcher") && lastPackageLock != null
		) {
			SharePreferenceUtils.getInstance().lastPackageLock = null
		}
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
					NotificationChannelGroup(CHANEL_GROUP_ID, CHANEL_GROUP_NAME)
				)
				val notificationChannel = NotificationChannel(
					CHANEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_HIGH
				)
				notificationChannel.enableLights(false)
				notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
				mNotificationManager?.createNotificationChannel(notificationChannel)
			}
			val builder = NotificationCompat.Builder(this, CHANEL_ID)

			builder.setContentTitle(
				StringBuilder(resources.getString(R.string.app_name)).append(" ")
					.append(resources.getString(R.string.msg_ntf_secure)).toString()
			).setSmallIcon(R.mipmap.ic_launcher_round)
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
		isServiceStarted = false
		AlarmUtils.setAlarm(this, AlarmUtils.ACTION_REPEAT_SERVICE, 1000)
	}

	@RequiresApi(Build.VERSION_CODES.M)
	override fun onTaskRemoved(rootIntent: Intent?) {
		super.onTaskRemoved(rootIntent)
		isServiceStarted = false
		AlarmUtils.setAlarm(this, AlarmUtils.ACTION_REPEAT_SERVICE, 1000)
	}
}