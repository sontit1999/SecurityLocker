package com.ls.entertainment.securitylocker.ui.trackingtime

import android.app.AppOpsManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.entertainment.basemvvmproject.base.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.adapter.UsageStateAdapter
import com.ls.entertainment.securitylocker.databinding.FragTrackingTimeBinding
import com.ls.entertainment.securitylocker.model.RefreshUsage
import com.ls.entertainment.securitylocker.utils.showAccessDataUsagePermissionDialog
import org.greenrobot.eventbus.EventBus


class TrackingTimeFragment :
	BaseFragment<FragTrackingTimeBinding, TrackingTimeViewModel>(R.layout.frag_tracking_time) {

	private val viewModel: TrackingTimeViewModel by viewModels()

	override fun getVM() = viewModel


	override fun viewCreated(savedInstanceState: Bundle?) {
		super.viewCreated(savedInstanceState)
		loadBannerAds()
		setUpViewPager()
		if (checkUsageStatsPermission()) {
			EventBus.getDefault().post(RefreshUsage())
		} else showDialogRequestPermissionUsage()
	}

	private fun loadBannerAds() {

	}


	override fun onResume() {
		super.onResume()
		EventBus.getDefault().post(RefreshUsage())
	}

	private fun checkUsageStatsPermission(): Boolean {
		val appOpsManager =
			requireActivity().getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager
		// `AppOpsManager.checkOpNoThrow` is deprecated from Android Q
		val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			appOpsManager.unsafeCheckOpNoThrow(
				"android:get_usage_stats", Process.myUid(), requireActivity().packageName
			)
		} else {
			appOpsManager.checkOpNoThrow(
				"android:get_usage_stats", Process.myUid(), requireActivity().packageName
			)
		}
		return mode == AppOpsManager.MODE_ALLOWED
	}

	private fun showDialogRequestPermissionUsage() {
		requireContext().showAccessDataUsagePermissionDialog(onOkListener = {
			requestPermissionUsage()
		}, onCancelListener = {
			showToast(getString(R.string.you_must_allow_permission))
		})
	}

	private fun requestPermissionUsage() {
		Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
			startActivity(this)
		}
	}

	private fun setUpViewPager() {
		binding.viewPagerUsage.adapter = UsageStateAdapter(requireActivity())
		TabLayoutMediator(binding.tabLayout, binding.viewPagerUsage) { tab, position ->
			if (position == 0) tab.text = getString(R.string.last_24_hour)
			if (position == 1) tab.text = getString(R.string.last_10_day)
		}.attach()
	}

}