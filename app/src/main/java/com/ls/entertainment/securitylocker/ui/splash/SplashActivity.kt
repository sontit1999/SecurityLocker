package com.ls.entertainment.securitylocker.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.viewModelScope
import com.entertainment.basemvvmproject.base.BaseActivity
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
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
import com.ls.entertainment.securitylocker.ui.intro.IntroActivity
import com.ls.entertainment.securitylocker.ui.unlock.UnlockActivity
import com.ls.entertainment.securitylocker.ui.unlock.UnlockActivity.Companion.KEY_TYPE_PASS
import com.ls.entertainment.securitylocker.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {

	private val viewModel: SplashViewModel by viewModels()

	override val layoutId = R.layout.activity_splash

	private var isShowDialogUpdate = false

	private var didGoToMain = false
	
	private var type = -1
	private var typeActionMenu = -1

	private var appOpenAd: AppOpenAd? = null

	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewModel.viewModelScope.launch(Dispatchers.IO) {
			loadRemoteConfig()
		}
		LockService.startLockService(this)
		type = intent.getIntExtra(KEY_TYPE_OPTIMIZE, -1)
		if (type == TYPE_FROM_UNLOCK) {
			typeActionMenu = intent.getIntExtra(KEY_ACTION_MENU_LOCK, -1)
		}
		trackingNotifyReceive()
		loadOpenAd()
	}

	private fun loadOpenAd() {
		// We will implement this below.
		// Have unused ad, no need to fetch another.

		// We will implement this below.
		// Have unused ad, no need to fetch another.
		   val loadCallback = object : AppOpenAdLoadCallback() {
			override fun onAdLoaded(ad: AppOpenAd) {
				appOpenAd = ad
				TrackingHelper.logEvent(AllEvents.E1_ADS_OPEN_ADS_LOAD_SUCCESS)
				showAdIfAvailable()
			}

			override fun onAdFailedToLoad(loadAdError: LoadAdError) {
				// Handle the error.
				TrackingHelper.logEvent(AllEvents.E1_ADS_OPEN_ADS_LOAD_FAIL)
				handleFlowGoMainActivity()
			}
		}
		val extras = Bundle()
		if (SharePreferenceUtils.getInstance().openCount <= 2) {
			extras.putString("max_ad_content_rating", "T")
		}
		val request = AdRequest.Builder().addNetworkExtrasBundle(
			AdMobAdapter::class.java, extras
		).build()
		AppOpenAd.load(
			App.instance,
			AppConstant.OPEN_AD_OPEN_APP_KEY,
			request,
			AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
			loadCallback
		)
	}

	fun showAdIfAvailable() {
		// Only show ad if there is not already an app open ad currently showing
		// and an ad is available.
		val fullScreenContentCallback: FullScreenContentCallback =
			object : FullScreenContentCallback() {
				override fun onAdDismissedFullScreenContent() {
					appOpenAd = null
					handleFlowGoMainActivity()
				}

				override fun onAdFailedToShowFullScreenContent(adError: AdError) {
					TrackingHelper.logEvent(AllEvents.E1_ADS_OPEN_ADS_SHOW_FAIL)
					handleFlowGoMainActivity()
				}

				override fun onAdShowedFullScreenContent() {
					TrackingHelper.logEvent(AllEvents.E1_ADS_OPEN_ADS_SHOW_SUCCESS)
				}

				override fun onAdClicked() {
					super.onAdClicked()
					TrackingHelper.logEvent(AllEvents.E1_ADS_OPEN_ADS_CLICKED)
				}
			}
		appOpenAd!!.fullScreenContentCallback = fullScreenContentCallback
		appOpenAd!!.show(this)
	}

	private fun trackingNotifyReceive() {
		TrackingHelper.logEvent(AllEvents.VIEW_SPLASH)
		val typeNotify = intent.getStringExtra(
			NotificationCenter.KEY_TAG_NOTIFY
		)
		when (typeNotify) {
			NotificationCenter.TAG_NOTIFY_OFFLINE -> TrackingHelper.logEvent(AllEvents.E1_NOTIFICATION_OFFLINE_CLICK)
			NotificationCenter.TAG_NOTIFY_FCM -> TrackingHelper.logEvent(AllEvents.E1_NOTIFICATION_FCM_CLICK)
			else -> {}
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
				val configJson =
					if (BuildConfig.DEBUG) remoteConfig.getString("config_debug") else remoteConfig.getString(
						"config"
					)
				RemoteConfig.configModel = ConfigModel.newInstance(configJson)
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
				RemoteConfig.commonConfig.resetConfig()
			}
		} catch (e: java.lang.Exception) {
			LogUtils.logCustomMessage(e.message.toString())
		}
	}

	private fun showDialogRequireUpdate() {
		TrackingHelper.logEvent(AllEvents.VIEW_UPDATE_APP)
		isShowDialogUpdate = true
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

	private fun handleFlowGoMainActivity() {
		if (isShowDialogUpdate && !didGoToMain) return
		didGoToMain = true
		val openCount = SharePreferenceUtils.getInstance().openCount
		SharePreferenceUtils.getInstance().openCount = openCount + 1
		
		if (openCount == 0) {
			TrackingHelper.logEvent(AllEvents.E1_OPEN_USER_FIRST_OPEN)
			val intent = Intent(this@SplashActivity, IntroActivity::class.java)
			startActivity(intent)
			finish()
		} else {
			TrackingHelper.logEvent(AllEvents.E1_OPEN_USER_REOPEN)
			if (!SharePreferenceUtils.getInstance().isSetupPass) {
				val intent = Intent(this@SplashActivity, UnlockActivity::class.java)
				intent.putExtra(KEY_TYPE_PASS, UnlockActivity.TYPE_SETUP_PASS)
				startActivity(intent)
				finish()
			} else goToMainActivity()
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