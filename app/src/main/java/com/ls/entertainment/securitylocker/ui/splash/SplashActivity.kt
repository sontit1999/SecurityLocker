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
import com.ls.entertainment.securitylocker.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {

	private val viewModel: SplashViewModel by viewModels()

	override val layoutId = R.layout.activity_splash

	private var isShowDialogUpdate = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		LockService.startLockService(this)
		CoroutineScope(Dispatchers.Main).launch {
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
				goMainActivity()
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
			goMainActivity()
		} catch (e: java.lang.Exception) {
			goMainActivity()
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

	private fun goMainActivity() {
		if (isShowDialogUpdate) return
		val openCount = SharePreferenceUtils.getInstance().openCount
		SharePreferenceUtils.getInstance().openCount = openCount + 1
		if ((openCount + 1) == 1 || !SharePreferenceUtils.getInstance().isSetupLanguage) {
			TrackingHelper.logEvent(AllEvents.E1_OPEN_USER_FIRST_OPEN)
			startActivity(Intent(this@SplashActivity, MainActivity::class.java))
			finish()
		} else {
			TrackingHelper.logEvent(AllEvents.E1_OPEN_USER_REOPEN)
			startActivity(Intent(this, MainActivity::class.java))
			finish()
		}

	}
}