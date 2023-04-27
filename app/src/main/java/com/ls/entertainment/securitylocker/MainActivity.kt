package com.ls.entertainment.securitylocker

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import com.entertainment.basemvvmproject.base.BaseActivity
import com.entertainment.basemvvmproject.utils.gone
import com.entertainment.basemvvmproject.utils.visible
import com.ls.entertainment.securitylocker.App.Companion.typeSetWallpaper
import com.ls.entertainment.securitylocker.adapter.MainViewPagerAdapter
import com.ls.entertainment.securitylocker.ads.AdManager
import com.ls.entertainment.securitylocker.databinding.ActivityMainBinding
import com.ls.entertainment.securitylocker.extension.canDrawOverlay
import com.ls.entertainment.securitylocker.extension.requestDrawOverlayPermission
import com.ls.entertainment.securitylocker.model.CheckPermissionEvent
import com.ls.entertainment.securitylocker.model.OpenAdEvent
import com.ls.entertainment.securitylocker.model.RefreshUsage
import com.ls.entertainment.securitylocker.service.LockService.Companion.startLockService
import com.ls.entertainment.securitylocker.ui.MainViewModel
import com.ls.entertainment.securitylocker.ui.batterysaver.BatterySaverFragment
import com.ls.entertainment.securitylocker.ui.splash.SplashActivity
import com.ls.entertainment.securitylocker.utils.*
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

	private val viewModel: MainViewModel by viewModels()

	override val layoutId = R.layout.activity_main

	override fun getVM() = viewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		EventBus.getDefault().register(this)
		AdManager.initialize()
		AdManager.loadBanner(binding.containerAds)
		startLockService(this)
		getDataFromIntent()
		checkPermissionApp()
		initViewPager()
		bindingAction()
		setUpObserver()
		scheduleLoadAd()
	}

	private fun scheduleLoadAd() {
		viewModel.viewModelScope.launch {
			while (true) {
				delay(10000)
				if (!NetworkListener.isNetWorkConnected()) {
					return@launch
				}
				AdManager.loadAdIfNeed(this@MainActivity)
				LogUtils.logCustomMessage("handle Load ads")
			}
		}
	}

	private fun setUpObserver() {

		RxBus.subscribe(TAG, OpenAdEvent::class) {
			if (it.isShow) {
				binding.containerAds.gone()
			} else binding.containerAds.visible()
		}

		NetworkListener.observe(this) {
			if (!it) {
				DialogUtil.showConfirmationNetworkDialog(this, okListener = {
					startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
				})
			}
		}
	}

	private fun getDataFromIntent() {
		when (intent.getIntExtra(SplashActivity.KEY_TYPE_OPTIMIZE, -1)) {
			SplashActivity.TYPE_FROM_FAST_CHARGER -> handleOpenFromFastCharge()
			SplashActivity.TYPE_FROM_UNLOCK       -> handleOpenFromUnlock()
			else                                  -> {}
		}
	}

	private fun handleOpenFromFastCharge() {
		addFragment(BatterySaverFragment())
	}

	private fun handleOpenFromUnlock() {
		viewModel.viewModelScope.launch {
			try {
				val typeAction = intent.getIntExtra(SplashActivity.KEY_ACTION_MENU_LOCK, -1)
				delay(1000)
				when (typeAction) {
					SplashActivity.ACTION_MENU_LOCK_APP     -> binding.bottomNavigation.selectedItemId =
						R.id.menu_app
					SplashActivity.ACTION_MENU_CHANGE_THEME -> binding.bottomNavigation.selectedItemId =
						R.id.menu_theme
					SplashActivity.ACTION_MENU_SETTING      -> binding.bottomNavigation.selectedItemId =
						R.id.menu_setting
					else                                    -> {}
				}
			} catch (e: Exception) {
				LogUtils.logCustomMessage(e.message.toString())
			}
		}
	}

	private fun bindingAction() {

		binding.bottomNavigation.setOnItemSelectedListener { item ->
			checkPermissionApp()
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
		requestCode: Int, permissions: Array<out String>, grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		LogUtils.logCustomMessage("onRequestPermissionsResult")
	}

	fun addFragment(fragment: Fragment) {
		supportFragmentManager.beginTransaction().add(R.id.mainContainer, fragment, fragment.tag)
			.addToBackStack(null).commit()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
			val resultUri = data?.let { UCrop.getOutput(it) }
			val path = resultUri?.toFile()?.path ?: ""
			val bitmap = BitmapFactory.decodeFile(path)
			setWallPaperFromBitMap(bitmap, path)
		} else if (resultCode == UCrop.RESULT_ERROR) {
			showToast(getString(R.string.fail_crop_image))
		}
	}

	private fun setWallPaperFromBitMap(bitmap: Bitmap?, path: String) {
		if (bitmap == null) {
			showToast(getString(R.string.fail_setwallpaper_message))
		} else {
			if (typeSetWallpaper == WallpaperUtils.WallpaperType.LOCK_APP) {
				viewModel.savePathImageLock(path)
			} else {
				val isSuccess = WallpaperUtils.setWallpaper(bitmap, typeSetWallpaper)
				if (isSuccess) {
					showToast(getString(R.string.congratulation_set_wallpaper_success))
				} else {
					showToast(getString(R.string.fail_setwallpaper_message))
				}
			}

		}

	}

	override fun onDestroy() {
		super.onDestroy()
		EventBus.getDefault().unregister(this)
		RxBus.unregister(TAG)
		AdManager.destroyAll()
		SharePreferenceUtils.getInstance().canShowOpenAd = false
	}

	companion object {
		const val TAG = "MainActivity"
	}

}