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
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.ls.entertainment.securitylocker.App.Companion.typeSetWallpaper
import com.ls.entertainment.securitylocker.adapter.MainViewPagerAdapter
import com.ls.entertainment.securitylocker.ads.AdManager
import com.ls.entertainment.securitylocker.databinding.ActivityMainBinding
import com.ls.entertainment.securitylocker.extension.canDrawOverlay
import com.ls.entertainment.securitylocker.extension.requestDrawOverlayPermission
import com.ls.entertainment.securitylocker.model.CheckPermissionEvent
import com.ls.entertainment.securitylocker.model.ConfigModel
import com.ls.entertainment.securitylocker.model.OpenAdEvent
import com.ls.entertainment.securitylocker.model.RefreshUsage
import com.ls.entertainment.securitylocker.service.LockService.Companion.startLockService
import com.ls.entertainment.securitylocker.ui.MainViewModel
import com.ls.entertainment.securitylocker.ui.batterysaver.BatterySaverFragment
import com.ls.entertainment.securitylocker.ui.splash.SplashActivity
import com.ls.entertainment.securitylocker.utils.AllEvents
import com.ls.entertainment.securitylocker.utils.AppUtils
import com.ls.entertainment.securitylocker.utils.DialogUtil
import com.ls.entertainment.securitylocker.utils.LogUtils
import com.ls.entertainment.securitylocker.utils.NetworkListener
import com.ls.entertainment.securitylocker.utils.RemoteConfig
import com.ls.entertainment.securitylocker.utils.RxBus
import com.ls.entertainment.securitylocker.utils.SharePreferenceUtils
import com.ls.entertainment.securitylocker.utils.TrackingHelper
import com.ls.entertainment.securitylocker.utils.WallpaperUtils
import com.ls.entertainment.securitylocker.utils.checkUsageStatsPermission
import com.ls.entertainment.securitylocker.utils.showAccessDataUsagePermissionDialog
import com.ls.entertainment.securitylocker.utils.showDrawOverlayPermissionDescDialog
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
		
		viewModel.stateSaveLockWallpaper.observe(this) {
			if (it) showToast(getString(R.string.msg_set_background_ok)) else showToast(getString(R.string.msg_set_background_fail))
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
		addFragment(BatterySaverFragment.newInstance(BatterySaverFragment.TYPE_OPTIMIZE_BATTERY))
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
				TrackingHelper.logEvent(AllEvents.ACTION_ALLOW_OVERLAY_DIALOG)
				requestDrawOverlayPermission(
					this, REQUEST_CODE_OVERLAY_PERMISSION
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
			TrackingHelper.logEvent(AllEvents.ACTION_ALLOW_USAGE_DIALOG)
			requestPermissionUsage()
		}, onCancelListener = {
			showToast(getString(R.string.you_must_allow_permission))
		})
	}

	private fun requestPermissionUsage() {
		Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
			startActivityForResult(this, REQUEST_CODE_USAGE_PERMISSION)
		}
	}


	@Subscribe
	fun checkPermissionEvent(checkPermissionEvent: CheckPermissionEvent) {
		checkPermissionApp()
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
		
		if (requestCode == REQUEST_CODE_USAGE_PERMISSION) {
			if (checkUsageStatsPermission()) {
				LogUtils.logCustomMessage("Allow usage permission")
				TrackingHelper.logEvent(AllEvents.PERMISSION_USAGE_ACCEPT)
			} else {
				LogUtils.logCustomMessage("Deny usage permission")
				TrackingHelper.logEvent(AllEvents.PERMISSION_USAGE_DENY)
			}
		} else if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
			if (canDrawOverlay()) {
				LogUtils.logCustomMessage("Allow overlay permission")
				TrackingHelper.logEvent(AllEvents.PERMISSION_OVERLAY_ACCEPT)
			} else {
				LogUtils.logCustomMessage("Deny overlay permission")
				TrackingHelper.logEvent(AllEvents.PERMISSION_OVERLAY_DENY)
			}
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
	
	override fun onResume() {
		super.onResume()
		loadRemoteConfig()
	}
	
	private fun loadRemoteConfig() {
		if (App.didLoadConfigSuccess) return
		val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
		val configSettings = remoteConfigSettings {
			minimumFetchIntervalInSeconds = 10
		}
		remoteConfig.setConfigSettingsAsync(configSettings)
		
		remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
			if (task.isSuccessful) {
				App.didLoadConfigSuccess = true
				val configJson = remoteConfig.getString("config")
				RemoteConfig.configModel = ConfigModel.newInstance(configJson)
				checkUpdateInSplashScreen()
				LogUtils.logCustomMessage("Load config success: $configJson")
				TrackingHelper.logEvent(AllEvents.E1_CONFIG_LOAD_SUCCESS)
			} else {
				TrackingHelper.logEvent(AllEvents.E1_CONFIG_LOAD_FAIL)
			}
		}
	}
	
	private fun checkUpdateInSplashScreen() {
		try {
			val list: List<String> = RemoteConfig.commonConfig.latestVersion.split("_")
			var isRequired = false
			var latestAppVersion = 0
			if (list.isNotEmpty()) {
				latestAppVersion = list[0].toInt()
				isRequired = list[1].contains("required")
			}
			if (latestAppVersion > BuildConfig.VERSION_CODE && isRequired) {
				showDialogRequireUpdate()
			}
			if (RemoteConfig.commonConfig.versionCodeForReview == BuildConfig.VERSION_CODE) {
				RemoteConfig.configModel = ConfigModel()
			}
		} catch (e: java.lang.Exception) {
			LogUtils.logCustomMessage(e.message.toString())
		}
	}
	
	private fun showDialogRequireUpdate() {
		TrackingHelper.logEvent(AllEvents.VIEW_UPDATE_APP)
		DialogUtil.showConfirmationDialog(this,
			getString(R.string.title_update_app),
			getString(R.string.desc_require_update_app),
			getString(R.string.msg_ok),
			"",
			okListener = {
				AppUtils.goToMarket(RemoteConfig.commonConfig.packageName, this)
				TrackingHelper.logEvent(AllEvents.E1_CLICK_UPDATE_APP)
				TrackingHelper.logEvent(AllEvents.ACTION_UPDATE)
			})
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
		const val REQUEST_CODE_USAGE_PERMISSION = 1999
		const val REQUEST_CODE_OVERLAY_PERMISSION = 2001
	}

}