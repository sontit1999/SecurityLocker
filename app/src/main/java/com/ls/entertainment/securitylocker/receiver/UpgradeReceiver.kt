package com.ls.entertainment.securitylocker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.ls.entertainment.securitylocker.service.LockService

class UpgradeReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent) {
		ContextCompat.startForegroundService(context, Intent(context, LockService::class.java))
	}
}