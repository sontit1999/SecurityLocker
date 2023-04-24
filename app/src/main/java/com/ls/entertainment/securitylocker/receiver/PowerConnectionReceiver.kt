package com.ls.entertainment.securitylocker.receiver

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ls.entertainment.securitylocker.ui.confirm.ConfirmActivity
import com.ls.entertainment.securitylocker.utils.LogUtils
import com.ls.entertainment.securitylocker.worker.PowerRestartWorker

class PowerConnectionReceiver : BroadcastReceiver() {
	override fun onReceive(p0: Context?, p1: Intent?) {
		if (p1?.action == Intent.ACTION_POWER_CONNECTED) {
			LogUtils.logCustomMessage("ACTION_POWER_CONNECTED")
			// Open app when plugged
			if (isInBackground()) {
				val intent = Intent(p0, ConfirmActivity::class.java).apply {
					addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
				}
				p0?.startActivity(intent)
			}
			PowerRestartWorker.schedule()
		} else if (p1?.action == Intent.ACTION_POWER_DISCONNECTED) {
			LogUtils.logCustomMessage("ACTION_POWER_DISCONNECTED")
			PowerRestartWorker.schedule()
		}
	}

	private fun isInBackground(): Boolean {
		val runningAppProcessInfo = ActivityManager.RunningAppProcessInfo()
		ActivityManager.getMyMemoryState(runningAppProcessInfo)
		return runningAppProcessInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
	}
}