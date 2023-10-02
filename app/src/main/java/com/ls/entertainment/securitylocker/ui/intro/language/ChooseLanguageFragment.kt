package com.ls.entertainment.securitylocker.ui.intro.language

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.entertainment.basemvvmproject.base.BaseFragment
import com.entertainment.basemvvmproject.base.LoadingDialog
import com.entertainment.basemvvmproject.utils.gone
import com.entertainment.basemvvmproject.utils.visible
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.ls.entertainment.securitylocker.MainActivity
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.adapter.LanguageAdapter
import com.ls.entertainment.securitylocker.ads.AdManager
import com.ls.entertainment.securitylocker.databinding.FragChooseLanguageBinding
import com.ls.entertainment.securitylocker.model.Language
import com.ls.entertainment.securitylocker.model.OpenAdEvent
import com.ls.entertainment.securitylocker.ui.unlock.UnlockActivity
import com.ls.entertainment.securitylocker.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ChooseLanguageFragment :
	BaseFragment<FragChooseLanguageBinding, ChooseLanguageViewModel>(R.layout.frag_choose_language) {

	var language = "en"
	private val viewModel: ChooseLanguageViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		TrackingHelper.logEvent(AllEvents.VIEW_LANGUAGE)
		if (RemoteConfig.commonConfig.isActiveAds) {
			if (RemoteConfig.commonConfig.supportBanner && RemoteConfig.commonConfig.supportBannerLanguage) {
				AdManager.loadBanner(
					binding.containerBannerAds,
					AppConstant.BANNER_LANGUAGE_KEY,
					RemoteConfig.commonConfig.supportBannerCollapseAllScreen
				)
			} else {
				if (RemoteConfig.commonConfig.supportNativeLanguage) {
					if (AppUtils.nativeAdChooseLanguageLiveData.value != null) {
						binding.nativeAdViews.binDataNativeAds(AppUtils.nativeAdChooseLanguageLiveData.value)
					} else loadNativeAd()
				} else binding.nativeAdViews.gone()
			}
		} else binding.nativeAdViews.gone()
		if (!RemoteConfig.commonConfig.supportNativeLanguage) binding.nativeAdViews.gone()
		initRecyclerviewLanguage()
		initEvent()
		setUpObserver()
	}

	private fun setUpObserver() {
		RxBus.subscribe(TAG, OpenAdEvent::class) {
			if (it.isShow) {
				binding.containerBannerAds.visibility = View.INVISIBLE
				binding.nativeAdViews.gone()
			} else {
				if (RemoteConfig.commonConfig.isActiveAds && RemoteConfig.commonConfig.supportNative && RemoteConfig.commonConfig.supportBannerLanguage) {
					binding.containerBannerAds.visible()
				}
				if (RemoteConfig.commonConfig.isActiveAds && RemoteConfig.commonConfig.supportNative && RemoteConfig.commonConfig.supportNativeLanguage) {
					binding.nativeAdViews.visible()
				}
			}
		}
	}

	private fun loadNativeAd() {
		val builder = AdLoader.Builder(requireContext(), AppConstant.NATIVE_CHOOSE_LANGUAGE)
		builder.forNativeAd { nativeAd ->
			binding.nativeAdViews.visible()
			binding.nativeAdViews.binDataNativeAds(nativeAd)
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

			override fun onAdFailedToLoad(p0: LoadAdError) {
				super.onAdFailedToLoad(p0)
				TrackingHelper.logEvent(AllEvents.E1_ADS_NATIVE_LANGUAGE_LOAD_FAIL)
			}
		}).build()
		adLoader.loadAd(AdManagerAdRequest.Builder().build())
	}


	private fun initEvent() {

	}

	private fun initRecyclerviewLanguage() {
		val adapter = LanguageAdapter(requireContext())
		binding.rvLanguage.adapter = adapter
		binding.rvLanguage.setHasFixedSize(true)
		binding.rvLanguage.layoutManager =
			LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
		adapter.setData(
			mutableListOf(
				Language(R.drawable.us, "English", "en"),
				Language(R.drawable.mx, "Spanish", "es"),
				Language(R.drawable.ic_de, "German", "de"),
				Language(R.drawable.vn, "Vietnamese", "vi"),
				Language(R.drawable.pt, "Portuguese", "pt"),
				Language(R.drawable.fr, "French", "fr"),
				Language(R.drawable.jp, "Japanese", "ja"),
				Language(R.drawable.kr, "Korean", "ko")
			)
		)
		adapter.onClickedItem = {
			language = it
			LoadingDialog.show(requireActivity().supportFragmentManager)
			viewModel.viewModelScope.launch {
				delay(1000)
				LoadingDialog.hidden(requireActivity().supportFragmentManager)
				goHomeActivity()
			}
		}

	}

	override fun onDestroy() {
		super.onDestroy()
		RxBus.unregister(TAG)
	}


	private fun goHomeActivity() {
		if (!SharePreferenceUtils.getInstance().isSetupPass) {
			val intent = Intent(requireActivity(), UnlockActivity::class.java)
			intent.putExtra(UnlockActivity.KEY_TYPE_PASS, UnlockActivity.TYPE_SETUP_PASS)
			startActivity(intent)
			requireActivity().finish()
		} else {
			val intent = Intent(requireActivity(), MainActivity::class.java)
			startActivity(intent)
		}
	}

	companion object {
		const val TAG = "ChooseLanguage"
		const val TAG_NATIVE = "native_choose_language"
		fun newInstance() = ChooseLanguageFragment()
	}

	override fun getVM(): ChooseLanguageViewModel {
		return viewModel
	}


}
