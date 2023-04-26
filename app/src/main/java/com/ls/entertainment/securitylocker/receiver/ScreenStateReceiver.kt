package com.ls.entertainment.securitylocker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.ls.entertainment.securitylocker.service.LockService

class ScreenStateReceiver : BroadcastReceiver() {
	override fun onReceive(p0: Context?, p1: Intent?) {
		if (p1?.action == Intent.ACTION_SCREEN_OFF || p1?.action == Intent.ACTION_USER_PRESENT || p1?.action == Intent.ACTION_SCREEN_ON) {
			Toast.makeText(p0, "onReceive ScreenStateReceiver", Toast.LENGTH_LONG).show()
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				p0?.startForegroundService(Intent(p0, LockService::class.java))
			} else {
				p0?.startService(Intent(p0, LockService::class.java))
			}
		}
	}

}