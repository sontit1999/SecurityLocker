package com.ls.entertainment.securitylocker.receiver

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.ui.confirm.ConfirmActivity
import com.ls.entertainment.securitylocker.utils.AllEvents
import com.ls.entertainment.securitylocker.utils.LogUtils
import com.ls.entertainment.securitylocker.utils.TrackingHelper
import com.ls.entertainment.securitylocker.worker.NotificationOneHourAfterUnplug
import com.ls.entertainment.securitylocker.worker.PowerRestartWorker

class PowerConnectionReceiver : BroadcastReceiver() {
	override fun onReceive(p0: Context?, p1: Intent?) {
		TrackingHelper.logEvent(AllEvents.POWER_BROADCAST_RECEIVE_ACTION + p1?.action)
		if (p1?.action == Intent.ACTION_POWER_CONNECTED) {
			LogUtils.logCustomMessage("ACTION_POWER_CONNECTED")
			saveBrightness(p0)
			// Open app when plugged
			if (isInBackground()) {
				val intent = Intent(p0, ConfirmActivity::class.java)
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
				p0?.startActivity(intent)
			}
			PowerRestartWorker.cancel()
			PowerRestartWorker.schedule()
		} else if (p1?.action == Intent.ACTION_POWER_DISCONNECTED) {
			p0?.let { handleRestoreBrightness(it) }
			LogUtils.logCustomMessage("ACTION_POWER_DISCONNECTED")
			PowerRestartWorker.cancel()
			PowerRestartWorker.schedule()
			scheduleAfter10mUnplug()
		}
	}

	private fun saveBrightness(ctx: Context?) {
		try {
			val brightness = Settings.System.getInt(
				ctx?.contentResolver, Settings.System.SCREEN_BRIGHTNESS
			)
			App.brightnessValue = brightness
		} catch (e: Exception) {
			LogUtils.logCustomMessage(e.message.toString())
		}
	}

	private fun handleRestoreBrightness(context: Context) {
		Settings.System.putInt(
			context.contentResolver,
			Settings.System.SCREEN_BRIGHTNESS_MODE,
			Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
		)
		val brightness = if (App.brightnessValue != 0) App.brightnessValue else 100
		Settings.System.putInt(
			context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness
		)
	}

	private fun scheduleAfter10mUnplug() {
		NotificationOneHourAfterUnplug.cancel()
		NotificationOneHourAfterUnplug.schedule()
	}

	private fun isInBackground(): Boolean {
		val runningAppProcessInfo = ActivityManager.RunningAppProcessInfo()
		ActivityManager.getMyMemoryState(runningAppProcessInfo)
		return runningAppProcessInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
	}
}