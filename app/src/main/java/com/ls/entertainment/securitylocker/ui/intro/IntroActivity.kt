package com.ls.entertainment.securitylocker.ui.intro

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.entertainment.basemvvmproject.base.BaseActivity

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.databinding.ActivityIntroBinding
import com.ls.entertainment.securitylocker.ui.intro.language.ChooseLanguageFragment
import com.ls.entertainment.securitylocker.utils.*

class IntroActivity : BaseActivity<ActivityIntroBinding, IntroViewModel>() {

	private val viewModel: IntroViewModel by viewModels()


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		loadNativeLanguage()
		addFragment(R.id.container, IntroFragment())
	}

	private fun loadNativeLanguage() {
		if (RemoteConfig.commonConfig.isActiveAds && RemoteConfig.commonConfig.supportNative && RemoteConfig.commonConfig.supportNativeLanguage) {
			loadNativeAd()
		}
	}

	private fun loadNativeAd() {
		val builder = AdLoader.Builder(this, AppConstant.NATIVE_CHOOSE_LANGUAGE)
		builder.forNativeAd { nativeAd ->
			AppUtils.nativeAdChooseLanguageLiveData.postValue(nativeAd)
		}
		val videoOptions = VideoOptions.Builder().setStartMuted(true).build()

		val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()

		builder.withNativeAdOptions(adOptions)
		val adLoader = builder.withAdListener(object : AdListener() {
			override fun onAdLoaded() {
				super.onAdLoaded()
				TrackingHelper.logEvent(AllEvents.E1_ADS_NATIVE_LANGUAGE_SHOWED)
			}

			override fun onAdClicked() {
				super.onAdClicked()
				TrackingHelper.logEvent(AllEvents.E1_ADS_NATIVE_LANGUAGE_CLICKED)
			}
		}).build()
		adLoader.loadAd(AdManagerAdRequest.Builder().build())
	}


	fun loadChooseLanguageFragment() {
		addFragment(R.id.container, ChooseLanguageFragment.newInstance())
	}

	override fun onBackPressed() {
		if (supportFragmentManager.fragments.last() is IntroFragment) {
			finish()
		} else supportFragmentManager.popBackStack()
	}

	private fun addFragment(container: Int, fragment: Fragment) {
		supportFragmentManager.beginTransaction()
			.add(container, fragment, fragment.javaClass.simpleName).addToBackStack(null).commit()
	}

	override val layoutId = R.layout.activity_intro
	override fun getVM() = viewModel
}