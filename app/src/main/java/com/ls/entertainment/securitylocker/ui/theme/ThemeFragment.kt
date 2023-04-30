package com.ls.entertainment.securitylocker.ui.theme

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.entertainment.basemvvmproject.base.BaseFragment
import com.entertainment.basemvvmproject.utils.RealPathUtil
import com.ls.entertainment.securitylocker.MainActivity
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.adapter.WallpaperModel
import com.ls.entertainment.securitylocker.databinding.FragThemeBinding
import com.ls.entertainment.securitylocker.model.OpenAdEvent
import com.ls.entertainment.securitylocker.ui.detail.DetailFragment
import com.ls.entertainment.securitylocker.utils.AllEvents
import com.ls.entertainment.securitylocker.utils.AppUtils
import com.ls.entertainment.securitylocker.utils.LogUtils
import com.ls.entertainment.securitylocker.utils.RxBus
import com.ls.entertainment.securitylocker.utils.TrackingHelper
import com.ls.entertainment.securitylocker.utils.setOnSafeClickListener

class ThemeFragment : BaseFragment<FragThemeBinding, ThemeViewModel>(R.layout.frag_theme) {
	
	private val viewModel: ThemeViewModel by viewModels()
	
	override fun getVM() = viewModel
	
	private val pickMedia =
		registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
			// Callback is invoked after the user selects a media item or closes the
			// photo picker.
			if (uri != null) {
				
				val path = RealPathUtil.getRealPath(requireContext(), uri)
				if (!path.isNullOrEmpty()) {
					LogUtils.logCustomMessage("Selected URI: $uri, realPath = $path")
					val wallpaperModel =
						WallpaperModel(System.currentTimeMillis().toInt(), path, path, true)
					val mainActivity = requireActivity() as? MainActivity?
					mainActivity?.addFragment(
						DetailFragment.newInstance(
							0,
							mutableListOf(wallpaperModel),
							wallpaperModel
						)
					)
				}
			} else {
				LogUtils.logCustomMessage("No media selected")
			}
		}
	
	override fun viewCreated(savedInstanceState: Bundle?) {
		super.viewCreated(savedInstanceState)
		initRecyclerView()
		viewModel.getImageLock()
	}
	
	override fun bindingAction() {
		super.bindingAction()
		binding.ivGallery.setOnSafeClickListener {
			if (!checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
				requestReadExternalStoragePermission()
			} else {
				// Registers a photo picker activity launcher in single-select mode.
				// Launch the photo picker and let the user choose only images.
				pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
				
			}
		}
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
			LogUtils.logCustomMessage("Permision storage ACEEPT")
			TrackingHelper.logEvent(AllEvents.PERMISSION_STORAGE_ACCEPT)
		} else {
			showToast("Permission deny")
			LogUtils.logCustomMessage("Permision storage Deny")
			TrackingHelper.logEvent(AllEvents.PERMISSION_STORAGE_DENY)
			showToast(getString(R.string.must_allow_storage_permission))
			AppUtils.goDetailInformationApp(
				requireContext(),
				packageName = requireActivity().packageName
			)
		}
	}
	
	private val requestPermissionLauncher = registerForActivityResult(
		ActivityResultContracts.RequestPermission()
	) { isGranted: Boolean ->
		if (isGranted) {
			LogUtils.logCustomMessage("Permision storage ACEEPT")
			TrackingHelper.logEvent(AllEvents.PERMISSION_STORAGE_ACCEPT)
		} else {
			// Permission not granted
			LogUtils.logCustomMessage("Permision storage Deny")
			TrackingHelper.logEvent(AllEvents.PERMISSION_STORAGE_DENY)
			showToast(getString(R.string.must_allow_storage_permission))
			AppUtils.goDetailInformationApp(
				requireContext(),
				packageName = requireActivity().packageName
			)
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