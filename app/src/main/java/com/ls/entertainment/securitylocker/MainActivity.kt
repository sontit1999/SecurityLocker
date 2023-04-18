package com.ls.entertainment.securitylocker

import android.app.AppOpsManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.example.demoandroidrikkei.base.ui.BaseActivityNotRequireViewModel
import com.ls.entertainment.securitylocker.databinding.ActivityMainBinding
import com.ls.entertainment.securitylocker.service.LockService

class MainActivity : BaseActivityNotRequireViewModel<ActivityMainBinding>() {

	override val layoutId = R.layout.activity_main

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		startLockService()
		if(!checkUsageStatsPermission()){
			requestPermissionUsage()
		}
		checkCanOverlayPermission()
	}

	private fun startLockService(){
		ContextCompat.startForegroundService(
			this, Intent(
				this, LockService::class.java
			)
		)
	}

	private fun checkCanOverlayPermission() {
		if (!canDrawOverlay()) {
			requestDrawOverlayPermission(
				this,
				999
			)
		}
	}

	private fun requestPermissionUsage() {
		Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
			startActivity(this)
		}
	}

	private fun checkUsageStatsPermission(): Boolean {
		val appOpsManager =
			getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager
		// `AppOpsManager.checkOpNoThrow` is deprecated from Android Q
		val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			appOpsManager.unsafeCheckOpNoThrow(
				"android:get_usage_stats", Process.myUid(), packageName
			)
		} else {
			appOpsManager.checkOpNoThrow(
				"android:get_usage_stats", Process.myUid(), packageName
			)
		}
		return mode == AppOpsManager.MODE_ALLOWED
	}
}