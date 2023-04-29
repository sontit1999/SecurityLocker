package com.ls.entertainment.securitylocker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.ls.entertainment.securitylocker.service.LockService
import com.ls.entertainment.securitylocker.utils.AllEvents
import com.ls.entertainment.securitylocker.utils.TrackingHelper

class UpgradeReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent) {
		TrackingHelper.logEvent(AllEvents.UPDATE_BROADCAST_RECEIVE_ACTION + intent.action)
		ContextCompat.startForegroundService(context, Intent(context, LockService::class.java))
	}
}