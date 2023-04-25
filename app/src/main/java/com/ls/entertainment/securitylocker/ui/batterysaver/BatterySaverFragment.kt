package com.ls.entertainment.securitylocker.ui.batterysaver

import android.Manifest
import android.animation.Animator
import android.app.ActivityManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.ContentResolver
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.entertainment.basemvvmproject.base.BaseFragment
import com.entertainment.basemvvmproject.utils.gone
import com.entertainment.basemvvmproject.utils.visible
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.databinding.FragBatterySaverBinding
import com.ls.entertainment.securitylocker.extension.canDrawOverlay
import com.ls.entertainment.securitylocker.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class BatterySaverFragment :
	BaseFragment<FragBatterySaverBinding, BatterySaverViewModel>(R.layout.frag_battery_saver) {

	private val viewModel: BatterySaverViewModel by viewModels()

	override fun getVM() = viewModel

	override fun viewCreated(savedInstanceState: Bundle?) {
		super.viewCreated(savedInstanceState)
		checkWriteSettingPermission()
	}

	private fun checkWriteSettingPermission() {
		if (requireActivity().canWriteSettings()) {
			startAnimationScanApp()
		} else {
			requireContext().showWriteSettingPermissionDescDialog(onOkListener = {
				requireContext().requestWriteSettingsPermission(
					this, RC_WRITE_SETTINGS
				)
			}, onCancelListener = {

			})
		}
	}

	private fun startAnimationScanApp() {

		binding.animScan.addAnimatorListener(object : Animator.AnimatorListener {
			override fun onAnimationStart(p0: Animator) {
				viewModel.viewModelScope.launch {
					val list = requireActivity().packageManager.getInstalledApplications(0).filter {
						requireActivity().packageManager.getLaunchIntentForPackage(it.packageName) != null
					}
					binding.tvProcess.text = list.size.toString()
					list.forEach {
						delay(200)
						binding.tvProcess.text = it.loadLabel(requireActivity().packageManager)
					}
				}
			}

			override fun onAnimationEnd(p0: Animator) {
				handleSaverBattery()
			}

			override fun onAnimationCancel(p0: Animator) {

			}

			override fun onAnimationRepeat(p0: Animator) {

			}

		})

		viewModel.viewModelScope.launch {
			if (!App.didOptimizeBatterySaver) {
				delay(500)
				binding.animScan.playAnimation()
			} else {
				showSuccessOptimize()
			}
		}
	}


	private fun handleSaverBattery() {
		binding.animScan.gone()
		binding.animSaverBattery.visible()
		binding.ivApp.visible()
		binding.animSaverBattery.apply {
			setAnimation(R.raw.scan)
			removeAllAnimatorListeners()
			cancelAnimation()
			repeatCount = 4
			addAnimatorListener(object : Animator.AnimatorListener {
				@RequiresApi(Build.VERSION_CODES.M)
				override fun onAnimationStart(p0: Animator) {
					binding.tvTitle.text = getString(R.string.optimizing)
					bindViewAvatar()
					saverBattery()
				}

				override fun onAnimationEnd(p0: Animator) {
					showSuccessOptimize()
				}

				override fun onAnimationCancel(p0: Animator) {

				}

				override fun onAnimationRepeat(p0: Animator) {

				}
			})
		}
		binding.animSaverBattery.playAnimation()
	}

	fun showSuccessOptimize() {
		binding.animScan.gone()
		binding.animSaverBattery.gone()
		binding.animSuccess.setAnimation(R.raw.animation_dones)
		binding.animSuccess.visible()
		binding.animSuccess.playAnimation()
		binding.tvProcess.gone()
		binding.ivApp.gone()
		binding.tvTitle.text = getString(R.string.opimize_battery_success)
	}

	private fun bindViewAvatar() {
		viewModel.viewModelScope.launch {
			val activityManager =
				requireActivity().getSystemService(ACTIVITY_SERVICE) as ActivityManager
			requireActivity().packageManager.getInstalledApplications(0).forEach {
				if (it.flags and ApplicationInfo.FLAG_SYSTEM != 1) {
					delay(300)
					val avatar = requireActivity().packageManager.getApplicationIcon(it.packageName)
					binding.ivApp.setImageDrawable(avatar)
					if (it.packageName != requireActivity().packageName) {
						activityManager.killBackgroundProcesses(it.packageName)
						optimizeParticularApp(it.packageName)
					}
				}
			}

		}
	}

	override fun bindingAction() {
		super.bindingAction()
		binding.container.setOnClickListener { }
		binding.ivBack.setOnSafeClickListener {
			requireActivity().onBackPressed()
		}
	}

	override fun bindingStateView() {
		super.bindingStateView()

		viewModel.nativeAdsLiveData.observe(viewLifecycleOwner) {
			binding.containerAds.visible()
			binding.containerAds.binDataNativeAds(it)
		}
	}

	@RequiresApi(Build.VERSION_CODES.M)
	private fun saverBattery() {
		updateBrightness()
		turnOffBlueTooth()
		turnOffAutoSync()
		killBackgroundApps()
		toggleAutoSync(false)
		toggleScreenRotation(0)
		CoroutineScope(Dispatchers.IO).launch {
			requireActivity().packageManager.getInstalledApplications(0).forEach {
				optimizeParticularApp(it.packageName)
			}
		}
	}

	private fun turnOffAutoSync() {
		try {
			ContentResolver.setMasterSyncAutomatically(false)
		} catch (e: Exception) {
			LogUtils.logCustomMessage(e.message.toString())
		}
	}


	@RequiresApi(Build.VERSION_CODES.M)
	private fun turnOffBlueTooth() {
		try {
			val bluetoothManager: BluetoothManager =
				requireActivity().getSystemService(BluetoothManager::class.java)
			val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
			if (bluetoothAdapter == null) {
				// Device doesn't support Bluetooth
				showToast(getString(R.string.no_support_bluetooth))
				return
			}
			if (bluetoothAdapter.isEnabled) {
				if (ActivityCompat.checkSelfPermission(
						requireContext(), Manifest.permission.BLUETOOTH_CONNECT
					) != PackageManager.PERMISSION_GRANTED
				) {

					return
				} else {
					bluetoothAdapter.disable()
				}

			}
		} catch (e: Exception) {
			LogUtils.logCustomMessage(e.message.toString())
		}
	}

	//Turn on Battery Optimization for a particular app
	private fun optimizeParticularApp(packageName: String) {
		val cmd = "dumpsys deviceidle whitelist +$packageName"
		Runtime.getRuntime().exec(cmd)
	}

	private fun updateBrightness() {
		if (requireContext().canWriteSettings()) {
			try {
				Settings.System.putInt(
					requireActivity().contentResolver,
					Settings.System.SCREEN_BRIGHTNESS_MODE,
					Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
				)
				val brightness = Settings.System.getInt(
					requireActivity().contentResolver, Settings.System.SCREEN_BRIGHTNESS
				)
				App.brightnessValue = brightness
				if (brightness < 40) return
				Settings.System.putInt(
					requireActivity().contentResolver, Settings.System.SCREEN_BRIGHTNESS, 40
				)
				requireActivity().window.changeAppScreenBrightnessValue(40F)

			} catch (e: Settings.SettingNotFoundException) {
				Log.e("Error", "Cannot access system brightness")
				e.printStackTrace()
			}
		}
	}

	private fun Window.changeAppScreenBrightnessValue(brightnessValue: Float) {
		val layoutParams = this.attributes
		layoutParams.screenBrightness = brightnessValue
		this.attributes = layoutParams
	}

	private fun killBackgroundApps() {
		try {
			val packages = requireContext().getInstalledApps()
			val activityManager =
				requireContext().getSystemService(ACTIVITY_SERVICE) as ActivityManager?
			activityManager?.run {
				for (packageInfo in packages) {
					if (packageInfo.packageName != requireContext().packageName) {
						killBackgroundProcesses(packageInfo.packageName)
					}
				}
			}
		} catch (e: Exception) {
			LogUtils.logCustomMessage(e.message.toString())
		}
	}

	private fun toggleBluetooth(enable: Boolean) {
		if (enable) {
			if (!requireContext().isBluetoothEnabled) {
				requireContext().toggleBluetooth(true)
			}
		} else {
			if (requireContext().isBluetoothEnabled) {
				requireContext().toggleBluetooth(false)
			}
		}
	}

	private fun toggleWifi(enable: Boolean) {
		if (enable) {
			if (!requireContext().isWifiEnabled) {
				requireContext().toggleWifi(true)
			}
		} else {
			if (requireContext().isWifiEnabled) {
				requireContext().toggleWifi(false)
			}
		}
	}

	private fun toggleAutoSync(enable: Boolean) {
		try {
			if (requireContext().isAutoSyncEnabled) {
				if (!enable) {
					requireContext().toggleAutoSync(false)
				}
			} else {
				if (enable) {
					requireContext().toggleAutoSync(true)
				}
			}
		} catch (e: Exception) {
			LogUtils.logCustomMessage(e.message.toString())
		}
	}

	private fun toggleScreenRotation(value: Int) {
		try {
			if (requireContext().canWriteSettings()) {
				if (requireContext().isAutoRotationEnabled) {
					if (value == 0) {
						requireContext().toggleAutoRotation(0)
					}
				} else {
					if (value == 1) {
						requireContext().toggleAutoRotation(1)
					}
				}
			}
		} catch (e: Exception) {
			LogUtils.logCustomMessage(e.message.toString())
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == RC_WRITE_SETTINGS && requireContext().canWriteSettings()) {
			startAnimationScanApp()
		} else if (requestCode == RC_DRAW_OVERLAY && requireContext().canDrawOverlay()) {

		} else {
			super.onActivityResult(requestCode, resultCode, data)
		}
	}

	companion object {
		const val RC_WRITE_SETTINGS = 256
		private const val RC_DRAW_OVERLAY = 257
	}
}