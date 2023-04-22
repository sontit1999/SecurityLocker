package com.ls.entertainment.securitylocker.service

import android.app.*
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import android.text.format.DateUtils
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.ls.entertainment.securitylocker.MainActivity
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.UnlockActivity
import com.ls.entertainment.securitylocker.model.BatteryModel
import com.ls.entertainment.securitylocker.utils.AlarmUtils
import com.ls.entertainment.securitylocker.utils.AppConstant.CHANEL_GROUP_ID
import com.ls.entertainment.securitylocker.utils.AppConstant.CHANEL_GROUP_NAME
import com.ls.entertainment.securitylocker.utils.AppConstant.CHANEL_ID
import com.ls.entertainment.securitylocker.utils.AppConstant.CHANEL_NAME
import com.ls.entertainment.securitylocker.utils.LogUtils
import com.ls.entertainment.securitylocker.utils.PermissionUtil
import com.ls.entertainment.securitylocker.utils.SharePreferenceUtils
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
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


	companion object {
		const val TAG = "LockService"
		fun startLockService(ctx: Context) {
			LogUtils.logCustomMessage(TAG, "LockService startLockService")
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				ContextCompat.startForegroundService(
					ctx, Intent(
						ctx, LockService::class.java
					)
				)

			} else {
				ctx.startService(
					Intent(
						ctx, LockService::class.java
					)
				)
			}

		}
	}

	override fun onBind(p0: Intent?): IBinder? {
		return null
	}

	@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
	override fun onCreate() {
		super.onCreate()
		LogUtils.logCustomMessage(TAG, "LockService onCreate")
		registerBroadCast()
		usageStageManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
		generateForegroundNotification()
	}

	private val receiver = object : BroadcastReceiver() {
		override fun onReceive(p0: Context?, p1: Intent?) {
			LogUtils.logCustomMessage(TAG, "receiver ${p1?.action} in lock service")
			when (p1?.action) {
				Intent.ACTION_BATTERY_CHANGED -> handleBatteryChange(p1)
			}
		}

	}

	private fun handleBatteryChange(intent: Intent) {
		val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
		val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
		val batteryPct: Float = if (scale != 0) {
			level * 100 / scale.toFloat()
		} else {
			0f
		}

		// Battery status - charging/not charging
		val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

		// Battery temperature
		val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1).toFloat().div(10)

		// Battery charger
		val chargePlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)

		// Battery health
		val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)

		// Battery technology
		val technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)

		// Battery capacity
		val batteryManager = if (PermissionUtil.isApi21orHigher()) {
			this.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
		} else {
			null
		}
		val chargeCounter = if (PermissionUtil.isApi21orHigher()) {
			batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
		} else {
			null
		}
		val capacity = if (PermissionUtil.isApi21orHigher()) {
			batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)?.let {
				if (it != 0) {
					(chargeCounter?.div(it) ?: 0) / 10
				} else {
					0
				}
			}
		} else {
			null
		}
		EventBus.getDefault().post(
			BatteryModel(
				health,
				capacity = capacity ?: 3000,
				temperature = temperature,
				technology = technology ?: "Li-On"
			)
		)
		LogUtils.logCustomMessage(
			TAG,
			"LockService handleBatteryChange status = $status, temparature = $temperature, chargePlugged = $chargePlugged, health =  $health, technology = $technology , capacity= $capacity , percent = $batteryPct  "
		)
	}

	private fun registerBroadCast() {
		val intentFilter = IntentFilter(Intent.ACTION_SCREEN_ON)
		intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
		intentFilter.addAction(Intent.ACTION_BATTERY_LOW)
		intentFilter.addAction(Intent.ACTION_BATTERY_OKAY)
		intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
		intentFilter.addAction(Intent.ACTION_POWER_CONNECTED)
		registerReceiver(receiver, intentFilter)
	}

	private fun unRegisterBroadCast() {
		unregisterReceiver(receiver)
	}

	@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		LogUtils.logCustomMessage(TAG, "onStartCommand in lock service")
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
			applicationContext.contentResolver, Settings.Secure.ANDROID_ID
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
						LogUtils.logCustomMessage(TAG, "[response bytes] ${String(bytes)}")
					} else {
						LogUtils.logCustomMessage(TAG, "[response error] ${error?.message}")
					}
				}
		} catch (e: Exception) {
			LogUtils.logCustomMessage(TAG, "Error making the request: ${e.message}")
		}
	}


	@OptIn(DelicateCoroutinesApi::class)
	@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
	private fun scheduleCheck() {
		GlobalScope.launch(Dispatchers.IO) {
			while (true) {
				delay(500)
				LogUtils.logCustomMessage(TAG, "LockService scheduleCheck")
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

			val notificationLayout = RemoteViews(packageName, R.layout.layout_custom_notify)
			notificationLayout.setTextViewText(
				R.id.tvTime, DateUtils.formatDateTime(
					this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
				)
			)
			val builder = NotificationCompat.Builder(this, CHANEL_ID)
			builder.setSmallIcon(R.drawable.icon_lock).setCustomContentView(notificationLayout)
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
		LogUtils.logCustomMessage(TAG, "LockService onDestroy")
		unRegisterBroadCast()
		isServiceStarted = false
		AlarmUtils.setAlarm(this, AlarmUtils.ACTION_REPEAT_SERVICE, 1000)
	}

	@RequiresApi(Build.VERSION_CODES.M)
	override fun onTaskRemoved(rootIntent: Intent?) {
		super.onTaskRemoved(rootIntent)
		LogUtils.logCustomMessage(TAG, "LockService onTaskRemoved")
		isServiceStarted = false
		AlarmUtils.setAlarm(this, AlarmUtils.ACTION_REPEAT_SERVICE, 1000)
	}
}