package com.ls.entertainment.securitylocker.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.entertainment.basemvvmproject.base.BaseActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.BuildConfig
import com.ls.entertainment.securitylocker.MainActivity
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.databinding.ActivitySplashBinding
import com.ls.entertainment.securitylocker.model.ConfigModel
import com.ls.entertainment.securitylocker.service.LockService
import com.ls.entertainment.securitylocker.ui.unlock.UnlockActivity
import com.ls.entertainment.securitylocker.ui.unlock.UnlockActivity.Companion.KEY_TYPE_PASS
import com.ls.entertainment.securitylocker.utils.AllEvents
import com.ls.entertainment.securitylocker.utils.AppUtils
import com.ls.entertainment.securitylocker.utils.DialogUtil
import com.ls.entertainment.securitylocker.utils.LogUtils
import com.ls.entertainment.securitylocker.utils.RemoteConfig
import com.ls.entertainment.securitylocker.utils.SharePreferenceUtils
import com.ls.entertainment.securitylocker.utils.TrackingHelper

class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {

	private val viewModel: SplashViewModel by viewModels()

	override val layoutId = R.layout.activity_splash

	private var isShowDialogUpdate = false

	private var didGoToMain = false

	private var type = -1
	private var typeActionMenu = -1

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		LockService.startLockService(this)
		type = intent.getIntExtra(KEY_TYPE_OPTIMIZE, -1)
		if (type == TYPE_FROM_UNLOCK) {
			typeActionMenu = intent.getIntExtra(KEY_ACTION_MENU_LOCK, -1)
		}
	}

	override fun onResume() {
		super.onResume()
		if (!didGoToMain) {
			loadRemoteConfig()
		}
	}

	override fun getVM() = viewModel

	private fun loadRemoteConfig() {
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
				handleFlowGoMainActivity()
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
			handleFlowGoMainActivity()
		} catch (e: java.lang.Exception) {
			handleFlowGoMainActivity()
			LogUtils.logCustomMessage(e.message.toString())
		}
	}

	private fun showDialogRequireUpdate() {
		isShowDialogUpdate = true
		DialogUtil.showConfirmationDialog(this,
			getString(R.string.title_update_app),
			getString(R.string.desc_require_update_app),
			getString(R.string.msg_ok),
			"",
			okListener = {
				AppUtils.goToMarket(RemoteConfig.commonConfig.packageName, this)
				TrackingHelper.logEvent(AllEvents.E1_CLICK_UPDATE_APP)
			})
	}

	private fun handleFlowGoMainActivity() {
		if (isShowDialogUpdate && !didGoToMain) return
		didGoToMain = true
		val openCount = SharePreferenceUtils.getInstance().openCount
		SharePreferenceUtils.getInstance().openCount = openCount + 1
		if (!SharePreferenceUtils.getInstance().isSetupPass) {
			val intent = Intent(this@SplashActivity, UnlockActivity::class.java)
			intent.putExtra(KEY_TYPE_PASS, UnlockActivity.TYPE_SETUP_PASS)
			startActivity(intent)
		} else if ((openCount + 1) == 1) {
			TrackingHelper.logEvent(AllEvents.E1_OPEN_USER_FIRST_OPEN)
			goToMainActivity()
		} else {
			TrackingHelper.logEvent(AllEvents.E1_OPEN_USER_REOPEN)
			goToMainActivity()
		}
		
		
	}

	private fun goToMainActivity() {
		val intent = Intent(this@SplashActivity, MainActivity::class.java)
		intent.putExtra(KEY_TYPE_OPTIMIZE, type)
		intent.putExtra(KEY_ACTION_MENU_LOCK, typeActionMenu)
		startActivity(intent)
		finish()
	}

	companion object {
		const val KEY_TYPE_OPTIMIZE = "KEY_TYPE_OPTIMIZE"
		const val TYPE_FROM_FAST_CHARGER = 1
		const val TYPE_FROM_UNLOCK = 2
		const val KEY_ACTION_MENU_LOCK = "KEY_ACTION_MENU_LOCK"
		const val ACTION_MENU_LOCK_APP = 45
		const val ACTION_MENU_CHANGE_THEME = 434
		const val ACTION_MENU_SETTING = 4534
	}
}