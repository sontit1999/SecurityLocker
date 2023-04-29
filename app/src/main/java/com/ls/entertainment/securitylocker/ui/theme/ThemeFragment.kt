package com.ls.entertainment.securitylocker.ui.theme

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.entertainment.basemvvmproject.base.BaseFragment
import com.ls.entertainment.securitylocker.MainActivity
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.databinding.FragThemeBinding
import com.ls.entertainment.securitylocker.model.OpenAdEvent
import com.ls.entertainment.securitylocker.ui.detail.DetailFragment
import com.ls.entertainment.securitylocker.utils.AllEvents
import com.ls.entertainment.securitylocker.utils.LogUtils
import com.ls.entertainment.securitylocker.utils.RxBus
import com.ls.entertainment.securitylocker.utils.TrackingHelper

class ThemeFragment : BaseFragment<FragThemeBinding, ThemeViewModel>(R.layout.frag_theme) {

	private val viewModel: ThemeViewModel by viewModels()

	override fun getVM() = viewModel

	override fun viewCreated(savedInstanceState: Bundle?) {
		super.viewCreated(savedInstanceState)
		initRecyclerView()
		viewModel.getImageLock()
	}

	private fun initRecyclerView() {
		binding.rvTheme.layoutManager =
			GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
		viewModel.adapter.onClickItem = { i, wallpaperModel ->
			val mainActivity = requireActivity() as? MainActivity?
			mainActivity?.addFragment(
				DetailFragment.newInstance(
					i,
					viewModel.adapter.getData(),
					wallpaperModel
				)
			)
		}
		viewModel.adapter.loadAds()
		binding.rvTheme.adapter = viewModel.adapter
	}

	private val requestAndroid10PermissionLauncher = registerForActivityResult(
		ActivityResultContracts.RequestMultiplePermissions()
	) { list: Map<String, Boolean> ->
		if (list.all { it.value }) {
		
		} else {
			showToast("Permission deny")

		}
	}

	private val requestPermissionLauncher = registerForActivityResult(
		ActivityResultContracts.RequestPermission()
	) { isGranted: Boolean ->
		if (isGranted) {
		
		} else {
			// Permission not granted

		}
	}

	private fun requestReadExternalStoragePermission() {
		if (!checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
			if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
				requestAndroid10PermissionLauncher.launch(
					arrayOf(
						Manifest.permission.READ_EXTERNAL_STORAGE,
						Manifest.permission.WRITE_EXTERNAL_STORAGE
					)
				)
			} else {
				requestPermissionLauncher.launch(
					Manifest.permission.READ_EXTERNAL_STORAGE
				)
			}
		}
	}

	override fun bindingStateView() {
		super.bindingStateView()
		RxBus.subscribe(TAG, OpenAdEvent::class) {
			viewModel.adapter.showOrHideAd(it.isShow)
		}
	}

	override fun onRequestPermissionsResult(
		requestCode: Int, permissions: Array<out String>, grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		LogUtils.logCustomMessage("onRequestPermissionsResult ${permissions.get(0)}")
	}

	override fun onResume() {
		super.onResume()
		TrackingHelper.logEvent(AllEvents.VIEW_THEME)
		requestReadExternalStoragePermission()
	}

	override fun onDestroy() {
		super.onDestroy()
		RxBus.unregister(TAG)
		viewModel.adapter.release()
	}

	companion object {
		const val TAG = "ThemeFragment"
	}

}