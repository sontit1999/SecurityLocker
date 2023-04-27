package com.ls.entertainment.securitylocker.ads

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import com.entertainment.basemvvmproject.utils.gone
import com.entertainment.basemvvmproject.utils.visible
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.custom.CustomNativeAdView
import com.ls.entertainment.securitylocker.model.InterAdEvent
import com.ls.entertainment.securitylocker.model.RewardAdEvent
import com.ls.entertainment.securitylocker.utils.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import org.greenrobot.eventbus.EventBus
import java.io.IOException

object AdManager {
	const val TAG_BACK_INTER_PDF = "TAG_BACK_INTER_PDF"
	const val TAG_BACK_INTER_ALL = "TAG_BACK_INTER_ALL"

	private var isDoingLoadInter = false
	private var interstitialAd: InterstitialAd? = null
	private var rewardedAd: RewardedAd? = null
	private var showedInterstitialLastTime = 0L
	var isShowInterOrReward = false
	private var isDoingLoadReward = false
	private var userEarnReward = false

	fun initialize() {
		AppOpenAdManager.start()
	}

	fun loadBanner(view: FrameLayout): AdView? {
		if (!RemoteConfig.commonConfig.isActiveAds || !RemoteConfig.commonConfig.supportBanner) return null
		try {
			val adView = AdView(view.context)
			adView.adListener = object : AdListener() {

				override fun onAdClicked() {
					super.onAdClicked()
					TrackingHelper.logEvent(AllEvents.E1_ADS_BANNER_CLICK)
				}

				override fun onAdLoaded() {
					view.visible()
					view.removeAllViews()
					val params = FrameLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
					)
					params.gravity = Gravity.BOTTOM
					view.addView(adView, params)
					TrackingHelper.logEvent(AllEvents.E1_ADS_BANNER_LOAD_SUCCESS)
					LogUtils.logCustomMessage("Banner load success")
				}

				override fun onAdFailedToLoad(p0: LoadAdError) {
					view.gone()
					TrackingHelper.logEvent(AllEvents.E1_ADS_BANNER_LOAD_FAIL)
					LogUtils.logCustomMessage("Banner load fail: ${p0.message}")
					super.onAdFailedToLoad(p0)
				}
			}
			adView.adUnitId = RemoteConfig.commonConfig.bannerAdKey
			adView.setAdSize(
				AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
					App.instance, (AppConfig.widthScreen / AppConfig.displayMetrics.density).toInt()
				)
			)
			adView.loadAd(buildAdRequest())
			return adView
		} catch (e: IOException) {
			LogUtils.logCustomMessage("Banner load fail: ${e.message}")
		}
		return null
	}

	private fun isInterAvailable() = interstitialAd != null
	private fun isRewardAvailable() = rewardedAd != null

	private fun handleLoadInter() {
		if (!RemoteConfig.commonConfig.supportInter || !RemoteConfig.commonConfig.isActiveAds) return
		if (isInterAvailable()) return
		if (isDoingLoadInter) return

		InterstitialAd.load(App.instance,
			RemoteConfig.commonConfig.interAdKey,
			buildAdRequest(),
			object : InterstitialAdLoadCallback() {
				override fun onAdLoaded(p0: InterstitialAd) {
					interstitialAd = p0
					isDoingLoadInter = false
					TrackingHelper.logEvent(AllEvents.E1_ADS_INTER_LOAD_SUCCESS)
					LogUtils.logCustomMessage("Inter ads load success")
				}

				override fun onAdFailedToLoad(p0: LoadAdError) {
					isDoingLoadInter = false
					interstitialAd = null
					TrackingHelper.logEvent(AllEvents.E1_ADS_INTER_LOAD_FAIL)
					LogUtils.logCustomMessage("Inter ads load fail:${p0.message}")
				}
			})
		isDoingLoadInter = true
	}

	private fun handleLoadReward() {
		if (!RemoteConfig.commonConfig.supportReward || !RemoteConfig.commonConfig.isActiveAds) return
		if (isRewardAvailable()) return
		if (isDoingLoadReward) return
		isDoingLoadReward = true
		RewardedAd.load(App.instance,
			RemoteConfig.commonConfig.rewardAdKey,
			buildAdRequest(),
			object : RewardedAdLoadCallback() {
				override fun onAdLoaded(p0: RewardedAd) {
					super.onAdLoaded(p0)
					rewardedAd = p0
					isDoingLoadReward = false
					TrackingHelper.logEvent(AllEvents.E1_ADS_REWARD_LOAD_SUCCESS)
					LogUtils.logCustomMessage("Reward ads load success")
				}

				override fun onAdFailedToLoad(p0: LoadAdError) {
					super.onAdFailedToLoad(p0)
					isDoingLoadReward = false
					rewardedAd = null
					TrackingHelper.logEvent(AllEvents.E1_ADS_REWARD_LOAD_FAIL)
					LogUtils.logCustomMessage("Reward ads load fail:${p0.message}")
				}
			})
	}

	fun showInter(
		isForced: Boolean = false, tag: String, onHidden: (() -> Unit)? = null
	): Boolean {
		if (!RemoteConfig.commonConfig.supportInter || !RemoteConfig.commonConfig.isActiveAds) return false
		val activity = AppOpenAdManager.currentActivity?.get()
		activity ?: return false
		return if (canShowInter || isForced) {
			if (interstitialAd == null) {
				TrackingHelper.logEvent(AllEvents.E1_ADS_INTER_SHOW_FAIL_NO_ADS)
				LogUtils.logCustomMessage("Inter show fail because inter = null")
				handleLoadInter()
				false
			} else {
				interstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {

					override fun onAdClicked() {
						super.onAdClicked()
						TrackingHelper.logEvent(AllEvents.E1_ADS_INTER_CLICKED)
					}

					override fun onAdFailedToShowFullScreenContent(p0: AdError) {
						onHidden?.invoke()
						EventBus.getDefault().post(InterAdEvent(false, tag))
						interstitialAd = null
						handleLoadInter()
						TrackingHelper.logEvent(AllEvents.E1_ADS_INTER_SHOW_FAIL)
						LogUtils.logCustomMessage("Inter show fail ")
					}

					override fun onAdDismissedFullScreenContent() {
						onHidden?.invoke()
						EventBus.getDefault().post(InterAdEvent(false, tag))
						isShowInterOrReward = false
						showedInterstitialLastTime = System.currentTimeMillis()
						interstitialAd = null
						handleLoadInter()
						LogUtils.logCustomMessage("Inter dismiss ")
					}

					override fun onAdShowedFullScreenContent() {
						isShowInterOrReward = true
						TrackingHelper.logEvent(AllEvents.E1_ADS_INTER_SHOW_SUCCESS)
						EventBus.getDefault().post(InterAdEvent(true, tag))
						LogUtils.logCustomMessage("Inter show success ")
					}

				}
				interstitialAd!!.show(activity)
				interstitialAd = null
				true
			}
		} else false
	}

	fun showRewarded(
		onHidden: (() -> Unit)? = null
	): Boolean {
		userEarnReward = false
		if (!RemoteConfig.commonConfig.supportReward || !RemoteConfig.commonConfig.isActiveAds) return false
		val activity = AppOpenAdManager.currentActivity?.get()
		activity ?: return false
		return if (!isRewardAvailable()) {
			TrackingHelper.logEvent(AllEvents.E1_ADS_REWARD_SHOW_FAIL_NO_ADS)
			LogUtils.logCustomMessage("Inter show fail because inter = null")
			handleLoadReward()
			false
		} else {
			rewardedAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {

				override fun onAdClicked() {
					super.onAdClicked()
					TrackingHelper.logEvent(AllEvents.E1_ADS_REWARD_CLICKED)
				}

				override fun onAdFailedToShowFullScreenContent(p0: AdError) {
					onHidden?.invoke()
					EventBus.getDefault().post(RewardAdEvent(false, "Reward"))
					rewardedAd = null
					handleLoadReward()
					TrackingHelper.logEvent(AllEvents.E1_ADS_REWARD_SHOW_FAIL)
					LogUtils.logCustomMessage("Reward show fail ")
				}

				override fun onAdDismissedFullScreenContent() {
					if (userEarnReward) onHidden?.invoke()
					EventBus.getDefault().post(RewardAdEvent(false, "Reward"))
					isShowInterOrReward = false
					rewardedAd = null
					handleLoadReward()
					LogUtils.logCustomMessage("Reward dismiss ")
				}

				override fun onAdShowedFullScreenContent() {
					isShowInterOrReward = true
					TrackingHelper.logEvent(AllEvents.E1_ADS_REWARD_SHOW_SUCCESS)
					EventBus.getDefault().post(RewardAdEvent(true, "Reward"))
					LogUtils.logCustomMessage("Reward show success ")
				}

			}
			rewardedAd!!.show(
				activity
			) {
				userEarnReward = true
				TrackingHelper.logEvent(AllEvents.E1_ADS_REWARD_USER_EARN_SUCCESS)
			}
			rewardedAd = null
			true
		}
	}

	fun updateLastTimeShowInter(lastTime: Long) {
		this.showedInterstitialLastTime = lastTime
	}

	private val canShowInter: Boolean
		get() {
			var milliseconds = RemoteConfig.commonConfig.waitingShowInter
			milliseconds *= 1000
			if (milliseconds < 0) return false
			val delta = System.currentTimeMillis() - showedInterstitialLastTime
			return delta > milliseconds || delta <= 0
		}

	fun loadNativeAd(): Single<NativeAd> {
		return Single.create<NativeAd> { emitter ->
			val builder = AdLoader.Builder(App.instance, RemoteConfig.commonConfig.nativeAdKey)
			val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
			val adOptions = com.google.android.gms.ads.nativead.NativeAdOptions.Builder()
				.setVideoOptions(videoOptions).build()

			builder.forNativeAd { unifiedNativeAd ->
				if (!emitter.isDisposed) {
					emitter.onSuccess(unifiedNativeAd)
				}
			}.withNativeAdOptions(adOptions)

			val adLoader = builder.withAdListener(object : AdListener() {

				override fun onAdFailedToLoad(p0: LoadAdError) {
					LogUtils.logCustomMessage(
						"Native Ad load fail: ${p0.message}"
					)
					TrackingHelper.logEvent(AllEvents.E1_ADS_NATIVE_LOAD_FAIL)
					if (!emitter.isDisposed) {
						emitter.onError(Exception())
					}
				}

				override fun onAdLoaded() {
					TrackingHelper.logEvent(AllEvents.E1_ADS_NATIVE_LOAD_SUCCESS)
					LogUtils.logCustomMessage("Native Ad load success")
				}

			}).build()
			adLoader.loadAd(buildAdRequest())
		}.observeOn(AndroidSchedulers.mainThread())
			.subscribeOn(io.reactivex.schedulers.Schedulers.io())
	}

	fun loadNativeAd(view: FrameLayout) {
		if (!RemoteConfig.commonConfig.isActiveAds || !RemoteConfig.commonConfig.supportNative) return
		val builder = AdLoader.Builder(App.instance, RemoteConfig.commonConfig.nativeAdKey)
		val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
		val adOptions = com.google.android.gms.ads.nativead.NativeAdOptions.Builder()
			.setVideoOptions(videoOptions).build()

		builder.forNativeAd { unifiedNativeAd ->
			view.visible()
			val customNativeAdView = CustomNativeAdView(view.context)
			customNativeAdView.binDataNativeAds(unifiedNativeAd)
			view.removeAllViews()
			view.addView(customNativeAdView)
		}.withNativeAdOptions(adOptions)

		val adLoader = builder.withAdListener(object : AdListener() {

			override fun onAdFailedToLoad(p0: LoadAdError) {
				view.gone()
				LogUtils.logCustomMessage(
					"Native Ad load fail: ${p0.message}"
				)
				TrackingHelper.logEvent(AllEvents.E1_ADS_NATIVE_LOAD_FAIL)
			}

			override fun onAdLoaded() {
				TrackingHelper.logEvent(AllEvents.E1_ADS_NATIVE_LOAD_SUCCESS)
				LogUtils.logCustomMessage("Native Ad load success")
			}

		}).build()
		adLoader.loadAd(buildAdRequest())
	}

	private fun buildAdRequest(): AdRequest {
		val extras = Bundle()
		return AdRequest.Builder().addNetworkExtrasBundle(
				AdMobAdapter::class.java, extras
			).build()
	}
	//endregion

	fun loadAdIfNeed(context: Context) {
		handleLoadInter()
		handleLoadReward()
		AppOpenAdManager.loadAd(context)
	}

	fun destroyAll() {
		interstitialAd = null
		AppOpenAdManager.release()
	}
}