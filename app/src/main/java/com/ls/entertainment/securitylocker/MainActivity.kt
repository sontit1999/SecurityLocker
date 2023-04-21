package com.ls.entertainment.securitylocker

import android.app.AppOpsManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import com.example.demoandroidrikkei.base.ui.BaseActivityNotRequireViewModel
import com.ls.entertainment.securitylocker.adapter.MainViewPagerAdapter
import com.ls.entertainment.securitylocker.databinding.ActivityMainBinding
import com.ls.entertainment.securitylocker.service.LockService.Companion.startLockService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivityNotRequireViewModel<ActivityMainBinding>() {

	override val layoutId = R.layout.activity_main

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		startLockService(this)
		initViewPager()
		bindingAction()
		checkPermissionApp()
	}

	private fun bindingAction() {
		binding.bottomNavigation.setOnItemSelectedListener { item ->
			when (item.itemId) {
				R.id.menu_app     -> {
					binding.viewPager.setCurrentItem(0, true)
					true
				}

				R.id.menu_tool    -> {
					binding.viewPager.setCurrentItem(1, true)
					true
				}

				R.id.menu_theme   -> {
					binding.viewPager.setCurrentItem(2, true)
					true
				}

				R.id.menu_setting -> {
					binding.viewPager.setCurrentItem(3, true)
					true
				}

				else              -> false
			}
		}
	}

	private fun checkPermissionApp() {
		if (!checkUsageStatsPermission()) {
			requestPermissionUsage()
		}
		checkCanOverlayPermission()
	}

	private fun initViewPager() {
		binding.viewPager.adapter = MainViewPagerAdapter(this)
		binding.viewPager.isUserInputEnabled = false
		binding.viewPager.offscreenPageLimit = 3
	}

	private fun checkCanOverlayPermission() {
		if (!canDrawOverlay()) {
			requestDrawOverlayPermission(
				this, 999
			)
		}
	}

	private fun requestPermissionUsage() {
		Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
			startActivity(this)
		}
	}

	private fun checkUsageStatsPermission(): Boolean {
		val appOpsManager = getSystemService(APP_OPS_SERVICE) as AppOpsManager
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