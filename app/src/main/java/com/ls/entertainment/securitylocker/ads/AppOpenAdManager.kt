package com.ls.entertainment.securitylocker.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.MainActivity
import com.ls.entertainment.securitylocker.model.OpenAdEvent
import com.ls.entertainment.securitylocker.utils.*
import com.ls.entertainment.securitylocker.worker.NotificationOfflineWorker
import com.ls.entertainment.securitylocker.worker.ScheduleRestartServiceEveryDayWorker
import java.lang.ref.WeakReference
import java.util.*

object AppOpenAdManager : Application.ActivityLifecycleCallbacks, LifecycleObserver {

	private var appOpenAd: AppOpenAd? = null
	private var isLoadingAd = false
	var isShowingAd = false
	var currentActivity: WeakReference<Activity>? = null

	/** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
	private var loadTime: Long = 0

	fun start() {
		App.instance.registerActivityLifecycleCallbacks(this)
		ProcessLifecycleOwner.get().lifecycle.addObserver(this)
	}

	/** Request an ad. */
	fun loadAd(context: Context) {

		if (!RemoteConfig.commonConfig.supportOpenAds || !RemoteConfig.commonConfig.isActiveAds) return

		// Do not load ad if there is an unused ad or one is already loading.
		if (isLoadingAd || isAdAvailable()) {
			return
		}

		isLoadingAd = true
		val request = AdRequest.Builder().build()
		AppOpenAd.load(context,
			RemoteConfig.commonConfig.openAdKey,
			request,
			AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
			object : AppOpenAd.AppOpenAdLoadCallback() {

				override fun onAdLoaded(ad: AppOpenAd) {
					// Called when an app open ad has loaded.
					LogUtils.logCustomMessage("Open ad load success")
					appOpenAd = ad
					isLoadingAd = false
					loadTime = Date().time
					TrackingHelper.logEvent(AllEvents.E1_ADS_OPEN_ADS_LOAD_SUCCESS)
				}

				override fun onAdFailedToLoad(loadAdError: LoadAdError) {
					// Called when an app open ad has failed to load.
					LogUtils.logCustomMessage("Open ad load fail : " + loadAdError.message)
					appOpenAd = null
					isLoadingAd = false
					TrackingHelper.logEvent(AllEvents.E1_ADS_OPEN_ADS_LOAD_FAIL)
				}
			})
	}

	private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
		val dateDifference: Long = Date().time - loadTime
		val numMilliSecondsPerHour: Long = 3600000
		return dateDifference < numMilliSecondsPerHour * numHours
	}

	/** Shows the ad if one isn't already showing. */
	private fun showAdIfAvailable(
		activity: Activity
	) {
		if (!SharePreferenceUtils.getInstance().canShowOpenAd) {
			SharePreferenceUtils.getInstance().canShowOpenAd = true
			return
		}
		if (!RemoteConfig.commonConfig.supportOpenAds || !RemoteConfig.commonConfig.isActiveAds) return
		// If the app open ad is already showing, do not show the ad again.
		if (isShowingAd || AdManager.isShowInterOrReward) {
			LogUtils.logCustomMessage("The app open ad is already showing")
			return
		}

		// If the app open ad is not available yet, invoke the callback then load the ad.
		if (!isAdAvailable()) {
			LogUtils.logCustomMessage("The app open ad is not ready yet")
			loadAd(activity)
			TrackingHelper.logEvent(AllEvents.E1_ADS_OPEN_ADS_SHOW_FAIL_NO_ADS)
			return
		}

		appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

			override fun onAdDismissedFullScreenContent() {
				RxBus.push(OpenAdEvent(false))
				// Called when full screen content is dismissed.
				// Set the reference to null so isAdAvailable() returns false.
				LogUtils.logCustomMessage("Open Ads: onAdDismissedFullScreenContent")
				appOpenAd = null
				isShowingAd = false
				loadAd(activity)
			}

			override fun onAdFailedToShowFullScreenContent(adError: AdError) {
				// Called when fullscreen content failed to show.
				// Set the reference to null so isAdAvailable() returns false.
				LogUtils.logCustomMessage("Open Ads: onAdFailedToShowFullScreenContent")
				appOpenAd = null
				loadAd(activity)
				TrackingHelper.logEvent(AllEvents.E1_ADS_OPEN_ADS_SHOW_FAIL)
			}
			
			override fun onAdShowedFullScreenContent() {
				RxBus.push(OpenAdEvent(true))
				appOpenAd = null
				isShowingAd = true
				// Called when fullscreen content is shown.
				LogUtils.logCustomMessage("Open Ads: onAdShowedFullScreenContent")
				TrackingHelper.logEvent(AllEvents.E1_ADS_OPEN_ADS_SHOW_SUCCESS)
			}
			
			override fun onAdClicked() {
				super.onAdClicked()
				TrackingHelper.logEvent(AllEvents.E1_ADS_OPEN_ADS_CLICKED)
			}
		}
		appOpenAd?.show(activity)
	}

	/** Check if ad exists and can be shown. */
	private fun isAdAvailable(): Boolean {
		return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
	}

	fun release() {
		appOpenAd = null
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_START)
	fun onStart() {
		Handler(Looper.getMainLooper()).postDelayed({
			currentActivity?.get()?.let {
				if (it is MainActivity) {
					showAdIfAvailable(it)
				}
			}
		}, 500)
	}

	override fun onActivityCreated(p0: Activity, p1: Bundle?) {

	}

	override fun onActivityStarted(p0: Activity) {
		currentActivity = WeakReference(p0)

	}

	override fun onActivityResumed(p0: Activity) {
		currentActivity = WeakReference(p0)
		SharePreferenceUtils.getInstance().indexNotification = 0
		NotificationOfflineWorker.cancel()
		ScheduleRestartServiceEveryDayWorker.cancel()
	}

	override fun onActivityPaused(p0: Activity) {
		LogUtils.logCustomMessage("onActivityPaused")
		NotificationOfflineWorker.schedule()
		ScheduleRestartServiceEveryDayWorker.schedule()
	}

	override fun onActivityStopped(p0: Activity) {

	}

	override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

	}

	override fun onActivityDestroyed(p0: Activity) {

	}

}