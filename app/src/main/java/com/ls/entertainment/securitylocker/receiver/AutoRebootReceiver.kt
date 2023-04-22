package com.ls.entertainment.securitylocker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.ls.entertainment.securitylocker.service.LockService
import com.ls.entertainment.securitylocker.utils.LogUtils

class AutoRebootReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent) {
		val action = intent.action
		if (action == "android.intent.action.BOOT_COMPLETED" || action == "android.intent.action.QUICKBOOT_POWERON" || action == "com.htc.intent.action.QUICKBOOT_POWERON" || action.equals(
				"android.intent.action.SCREEN_ON", ignoreCase = true
			) || action.equals(
				"android.intent.action.USER_PRESENT", ignoreCase = true
			) || action.equals(
				"android.intent.action.ACTION_POWER_CONNECTED", ignoreCase = true
			) || action.equals(
				"android.net.wifi.WIFI_STATE_CHANGED", ignoreCase = true
			) || action.equals(
				"android.net.conn.CONNECTIVITY_CHANGE", ignoreCase = true
			) || action.equals("android.net.wifi.STATE_CHANGE", ignoreCase = true)
		) {
			try {
				LogUtils.logCustomMessage("AutoRebootReceiver")
				ContextCompat.startForegroundService(
					context,
					Intent(context, LockService::class.java)
				)
			} catch (e: Exception) {
				LogUtils.logCustomMessage(e.message.toString())
			}
		}
	}
}