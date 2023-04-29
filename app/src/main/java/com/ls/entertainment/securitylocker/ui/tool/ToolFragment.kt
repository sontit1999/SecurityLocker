package com.ls.entertainment.securitylocker.ui.tool

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.BatteryManager.BATTERY_HEALTH_COLD
import android.os.BatteryManager.BATTERY_HEALTH_DEAD
import android.os.BatteryManager.BATTERY_HEALTH_GOOD
import android.os.BatteryManager.BATTERY_HEALTH_OVERHEAT
import android.os.BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE
import android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY
import android.os.BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER
import android.os.BatteryManager.EXTRA_HEALTH
import android.os.BatteryManager.EXTRA_LEVEL
import android.os.BatteryManager.EXTRA_PLUGGED
import android.os.BatteryManager.EXTRA_SCALE
import android.os.BatteryManager.EXTRA_STATUS
import android.os.BatteryManager.EXTRA_TECHNOLOGY
import android.os.BatteryManager.EXTRA_TEMPERATURE
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.entertainment.basemvvmproject.base.BaseFragment
import com.entertainment.basemvvmproject.utils.gone
import com.entertainment.basemvvmproject.utils.visible
import com.ls.entertainment.securitylocker.MainActivity
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.databinding.FragToolBinding
import com.ls.entertainment.securitylocker.service.LockService
import com.ls.entertainment.securitylocker.ui.batterysaver.BatterySaverFragment
import com.ls.entertainment.securitylocker.ui.trackingtime.TrackingTimeFragment
import com.ls.entertainment.securitylocker.utils.AppUtils
import com.ls.entertainment.securitylocker.utils.LogUtils
import com.ls.entertainment.securitylocker.utils.PermissionUtil
import com.ls.entertainment.securitylocker.utils.RemoteConfig
import com.ls.entertainment.securitylocker.utils.setOnSafeClickListener

class ToolFragment : BaseFragment<FragToolBinding, ToolViewModel>(R.layout.frag_tool) {

	private val viewModel: ToolViewModel by viewModels()

	private val receiver = object : BroadcastReceiver() {
		override fun onReceive(p0: Context?, p1: Intent?) {
			LogUtils.logCustomMessage(LockService.TAG, "receiver ${p1?.action} in lock service")
			when (p1?.action) {
				Intent.ACTION_BATTERY_CHANGED -> handleBatteryChange(p1)
			}
		}

	}

	override fun getVM() = viewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		//EventBus.getDefault().register(this)
		registerBroadCast()
	}

	private fun handleBatteryChange(intent: Intent) {
		val level: Int = intent.getIntExtra(EXTRA_LEVEL, -1)
		val scale: Int = intent.getIntExtra(EXTRA_SCALE, -1)
		val batteryPct: Float = if (scale != 0) {
			level * 100 / scale.toFloat()
		} else {
			0f
		}
		
		// Battery status - charging/not charging
		val status = intent.getIntExtra(EXTRA_STATUS, -1)
		
		// Battery temperature
		val temperature = intent.getIntExtra(EXTRA_TEMPERATURE, -1).toFloat().div(10)

		// Battery charger
		val chargePlugged = intent.getIntExtra(EXTRA_PLUGGED, -1)

		// Battery health
		val health = intent.getIntExtra(EXTRA_HEALTH, -1)

		// Battery technology
		val technology = intent.getStringExtra(EXTRA_TECHNOLOGY)

		// Battery capacity
		val batteryManager = if (PermissionUtil.isApi21orHigher()) {
			requireActivity().getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
		} else {
			null
		}
		val chargeCounter = if (PermissionUtil.isApi21orHigher()) {
			batteryManager?.getIntProperty(BATTERY_PROPERTY_CHARGE_COUNTER)
		} else {
			null
		}
		val capacity = if (PermissionUtil.isApi21orHigher()) {
			batteryManager?.getIntProperty(BATTERY_PROPERTY_CAPACITY)?.let {
				if (it != 0) {
					(chargeCounter?.div(it) ?: 0) / 10
				} else {
					0
				}
			}
		} else {
			null
		}
		binding.tvContentChargeCapacity.text = capacity.toString() + getString(R.string.miliampe)
		binding.tvContentChargeTemperature.text = "$temperature° C"
		binding.tvContentChargeType.text = technology.toString()
		binding.tvContentHealth.text = getBatteryHealth(health)
		updateStatusRam()
	}

	private fun updateStatusRam() {

		val totalRam = AppUtils.getTotalRam()
		val ramFree = AppUtils.getAvailableRam(requireContext())
		val ramUsed = totalRam - ramFree
		val percentRam = ((ramFree.toFloat() / totalRam) * 100).toInt()
		LogUtils.logCustomMessage(
			"Sontv",
			"total = ${AppUtils.formatSize(totalRam)},free = ${AppUtils.formatSize(ramFree)},ramUsed = ${
				AppUtils.formatSize(ramUsed)
			}, percentRam = $percentRam"
		)
		binding.tvContentRam.text = "$percentRam%"
		if (percentRam <= 5) {
			binding.waveLoadingView.waveColor = ContextCompat.getColor(
				requireContext(), R.color.battery_almost_die
			)
			binding.waveLoadingView.progressValue = percentRam
		} else if (percentRam in 6..15) {
			binding.waveLoadingView.waveColor = ContextCompat.getColor(
				requireContext(), R.color.battery_bad
			)
			binding.waveLoadingView.progressValue = percentRam
		} else if (percentRam in 16..30) {
			binding.waveLoadingView.waveColor = ContextCompat.getColor(
				requireContext(), R.color.battery_averange
			)
			binding.waveLoadingView.progressValue = percentRam
		} else if (percentRam in 31..60) {
			binding.waveLoadingView.waveColor = ContextCompat.getColor(
				requireContext(), R.color.battery_good
			)
			binding.waveLoadingView.progressValue = percentRam
		} else if (percentRam in 61..100) {
			binding.waveLoadingView.waveColor = ContextCompat.getColor(
				requireContext(), R.color.battery_good
			)
			binding.waveLoadingView.progressValue = percentRam
		}
	}

	private fun getBatteryHealth(health: Int): String {
		return when (health) {
			BATTERY_HEALTH_GOOD                -> getString(R.string.battery_health_good)
			BATTERY_HEALTH_DEAD                -> getString(R.string.battery_health_dead)
			BATTERY_HEALTH_COLD                -> getString(R.string.battery_health_cold)
			BATTERY_HEALTH_OVERHEAT            -> getString(R.string.battery_health_overheat)
			BATTERY_HEALTH_UNSPECIFIED_FAILURE -> getString(R.string.battery_health_unspecified_failure)
			else                               -> getString(R.string.battery_health_unkown)
		}
	}

	override fun bindingAction() {
		super.bindingAction()
		binding.btnOptimizeRam.setOnSafeClickListener {
			(requireActivity() as? MainActivity?)?.addFragment(
				BatterySaverFragment.newInstance(
					BatterySaverFragment.TYPE_OPTIMIZE_BOOSTER
				)
			)
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		if (RemoteConfig.commonConfig.supportBoostedRam) {
			binding.btnOptimizeRam.visible()
		} else binding.btnOptimizeRam.gone()
		loadFragment()
	}

	private fun loadFragment() {
		val fragment = TrackingTimeFragment()
		childFragmentManager.beginTransaction().add(R.id.container, fragment).commit()
	}

	private fun registerBroadCast() {
		val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
		requireActivity().registerReceiver(receiver, intentFilter)
	}

	private fun unRegisterBroadCast() {
		requireActivity().unregisterReceiver(receiver)
	}

	override fun onDestroy() {
		super.onDestroy()
		unRegisterBroadCast()
		//EventBus.getDefault().unregister(this)
	}

}