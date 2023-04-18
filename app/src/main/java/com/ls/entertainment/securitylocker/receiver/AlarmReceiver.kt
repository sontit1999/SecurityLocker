package com.ls.entertainment.securitylocker.receiver

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import androidx.core.content.ContextCompat
import com.ls.entertainment.securitylocker.utils.AlarmUtils
import com.ls.entertainment.securitylocker.service.LockService

class AlarmReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent) {
		if (intent.action == AlarmUtils.ACTION_AUTOSTART_ALARM) {
			val newWakeLock =
				(context.getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(
					1,
					"AppName:NAG"
				)
			newWakeLock.acquire(10 * 60 * 1000L /*10 minutes*/)
			val extras = intent.extras
			if (extras != null) {
				if (extras.getBoolean(
						AlarmUtils.ACTION_REPEAT_SERVICE,
						false
					) && !isMyServiceRunning(
						LockService::class.java, context
					)
				) {
					ContextCompat.startForegroundService(
						context,
						Intent(context, LockService::class.java)
					)
				}
			}
			newWakeLock.release()
		}
	}

	private fun isMyServiceRunning(cls: Class<*>, context: Context): Boolean {
		for (runningServiceInfo in (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(
			Int.MAX_VALUE
		)) {
			if (cls.name == runningServiceInfo.service.className) {
				return true
			}
		}
		return false
	}
}