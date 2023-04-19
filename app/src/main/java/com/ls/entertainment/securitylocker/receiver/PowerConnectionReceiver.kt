package com.ls.entertainment.securitylocker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ls.entertainment.securitylocker.utils.LogUtils
import com.ls.entertainment.securitylocker.worker.PowerRestartWorker

class PowerConnectionReceiver : BroadcastReceiver() {
	override fun onReceive(p0: Context?, p1: Intent?) {
		if (p1?.action == Intent.ACTION_POWER_CONNECTED) {
			LogUtils.logCustomMessage("ACTION_POWER_CONNECTED")
			PowerRestartWorker.schedule()
		} else if (p1?.action == Intent.ACTION_POWER_DISCONNECTED) {
			LogUtils.logCustomMessage("ACTION_POWER_DISCONNECTED")
			PowerRestartWorker.schedule()
		}
	}
}