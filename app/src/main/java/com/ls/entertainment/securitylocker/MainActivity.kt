package com.ls.entertainment.securitylocker

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import com.example.demoandroidrikkei.base.ui.BaseActivityNotRequireViewModel
import com.ls.entertainment.securitylocker.adapter.MainViewPagerAdapter
import com.ls.entertainment.securitylocker.databinding.ActivityMainBinding
import com.ls.entertainment.securitylocker.extension.canDrawOverlay
import com.ls.entertainment.securitylocker.extension.requestDrawOverlayPermission
import com.ls.entertainment.securitylocker.model.CheckPermissionEvent
import com.ls.entertainment.securitylocker.model.RefreshUsage
import com.ls.entertainment.securitylocker.service.LockService.Companion.startLockService
import com.ls.entertainment.securitylocker.utils.LogUtils
import com.ls.entertainment.securitylocker.utils.checkUsageStatsPermission
import com.ls.entertainment.securitylocker.utils.showAccessDataUsagePermissionDialog
import com.ls.entertainment.securitylocker.utils.showDrawOverlayPermissionDescDialog
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

@AndroidEntryPoint
class MainActivity : BaseActivityNotRequireViewModel<ActivityMainBinding>() {
	
	override val layoutId = R.layout.activity_main
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		EventBus.getDefault().register(this)
		startLockService(this)
		checkPermissionApp()
		initViewPager()
		bindingAction()
	}
	
	private fun bindingAction() {
		
		binding.bottomNavigation.setOnItemSelectedListener { item ->
			checkPermissionApp()
			when (item.itemId) {
				R.id.menu_app -> {
					binding.viewPager.setCurrentItem(0, true)
					true
				}
				
				R.id.menu_tool -> {
					binding.viewPager.setCurrentItem(1, true)
					true
				}
				
				R.id.menu_theme -> {
					binding.viewPager.setCurrentItem(2, true)
					true
				}
				
				R.id.menu_setting -> {
					binding.viewPager.setCurrentItem(3, true)
					true
				}
				
				else -> false
			}
		}
	}
	
	private fun checkPermissionApp() {
		checkCanOverlayPermission()
		checkUsagePermission()
	}
	
	private fun initViewPager() {
		binding.viewPager.adapter = MainViewPagerAdapter(this)
		binding.viewPager.isUserInputEnabled = false
		binding.viewPager.offscreenPageLimit = 3
	}
	
	private fun checkCanOverlayPermission() {
		if (!canDrawOverlay()) {
			showDrawOverlayPermissionDescDialog(onOkListener = {
				requestDrawOverlayPermission(
					this, 999
				)
			}, onCancelListener = {
			
			})
		}
	}
	
	private fun checkUsagePermission() {
		if (checkUsageStatsPermission()) {
			EventBus.getDefault().post(RefreshUsage())
		} else showDialogRequestPermissionUsage()
	}
	
	private fun showDialogRequestPermissionUsage() {
		showAccessDataUsagePermissionDialog(onOkListener = {
			requestPermissionUsage()
		}, onCancelListener = {
			showToast(getString(R.string.you_must_allow_permission))
		})
	}
	
	private fun requestPermissionUsage() {
		Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
			startActivity(this)
		}
	}
	
	
	@Subscribe
	fun checkPermissionEvent(checkPermissionEvent: CheckPermissionEvent) {
		checkPermissionApp()
	}
	
	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		LogUtils.logCustomMessage("onRequestPermissionsResult")
	}
	
	fun addFragment(fragment: Fragment) {
		supportFragmentManager.beginTransaction().add(R.id.mainContainer, fragment, fragment.tag)
			.addToBackStack(null).commit()
	}
	
	override fun onDestroy() {
		super.onDestroy()
		EventBus.getDefault().unregister(this)
	}
}