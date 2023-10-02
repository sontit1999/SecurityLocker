package com.ls.entertainment.securitylocker.ui.intro

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.entertainment.basemvvmproject.base.BaseFragment
import com.entertainment.basemvvmproject.utils.gone
import com.entertainment.basemvvmproject.utils.visible
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.ads.AdManager
import com.ls.entertainment.securitylocker.databinding.FragIntroBinding
import com.ls.entertainment.securitylocker.model.OpenAdEvent
import com.ls.entertainment.securitylocker.utils.*


class IntroFragment : BaseFragment<FragIntroBinding, IntroViewModel>(R.layout.frag_intro) {

	private val viewModel : IntroViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setUpObserver()
	}

	private fun setUpObserver() {
		RxBus.subscribe(TAG, OpenAdEvent::class) {
			if (it.isShow) {
				binding.containerBannerAds.gone()
			} else binding.containerBannerAds.visible()
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		TrackingHelper.logEvent(AllEvents.VIEW_INTRO)
		AdManager.loadBanner(binding.containerBannerAds, AppConstant.BANNER_INTRO_KEY, RemoteConfig.commonConfig.supportBannerCollapseAllScreen)
		Glide.with(binding.ivIntro.context).load(RemoteConfig.commonConfig.urlImageIntro).into(
			binding.ivIntro
		)
		binding.btnStartNow.setOnClickListener { view -> (requireActivity() as? IntroActivity)?.loadChooseLanguageFragment() }
	}


	override fun onDestroy() {
		super.onDestroy()
		RxBus.unregister(TAG)
	}

	override fun getVM() = viewModel

	companion object{
		const val TAG = "intro"
	}
}