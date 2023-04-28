package com.ls.entertainment.securitylocker.ui.policy

import android.os.Bundle
import android.view.View
import com.entertainment.basemvvmproject.utils.gone
import com.entertainment.basemvvmproject.utils.visible
import com.entertainment.demoandroidrikkei.base.ui.BaseFragmentNotRequireViewModel
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.ads.AdManager
import com.ls.entertainment.securitylocker.databinding.FragPolicyBinding
import com.ls.entertainment.securitylocker.model.OpenAdEvent
import com.ls.entertainment.securitylocker.ui.detail.DetailFragment
import com.ls.entertainment.securitylocker.utils.AppUtils
import com.ls.entertainment.securitylocker.utils.RxBus
import com.ls.entertainment.securitylocker.utils.setOnSafeClickListener

class PolicyFragment : BaseFragmentNotRequireViewModel<FragPolicyBinding>(R.layout.frag_policy) {

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		AdManager.loadBanner(binding.containerAds)
		binding.tvPolicy.text = AppUtils.readPolicyFromAsset("policy.txt")
		binding.ivBack.setOnSafeClickListener {
			requireActivity().onBackPressed()
		}
		binding.container.setOnSafeClickListener {  }
	}

	override fun bindingStateView() {
		super.bindingStateView()
		RxBus.subscribe(TAG, OpenAdEvent::class) {
			if (it.isShow) {
				binding.containerAds.gone()
			} else binding.containerAds.visible()
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		RxBus.unregister(TAG)
	}

	companion object{
		const val TAG = "Policy"
	}
}