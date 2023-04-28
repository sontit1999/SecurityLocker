package com.ls.entertainment.securitylocker.ui.setting

import android.content.Intent
import androidx.fragment.app.viewModels
import com.entertainment.basemvvmproject.base.BaseFragment
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.databinding.FragSettingBinding
import com.ls.entertainment.securitylocker.ui.unlock.UnlockActivity
import com.ls.entertainment.securitylocker.utils.AllEvents
import com.ls.entertainment.securitylocker.utils.AppUtils.goToMarket
import com.ls.entertainment.securitylocker.utils.AppUtils.sendFeedBack
import com.ls.entertainment.securitylocker.utils.AppUtils.shareApp
import com.ls.entertainment.securitylocker.utils.TrackingHelper

class SettingFragment : BaseFragment<FragSettingBinding, SettingViewModel>(R.layout.frag_setting) {

	private val viewModel: SettingViewModel by viewModels()

	override fun getVM() = viewModel

	override fun bindingAction() {
		super.bindingAction()
		binding.btnUpdate.setOnClickListener {
			if (isDoubleClick) return@setOnClickListener
			TrackingHelper.logEvent(AllEvents.E1_CLICK_UPDATE_APP)
			goToMarket(requireActivity().packageName, requireContext())
		}
		binding.btnShare.setOnClickListener {
			if (isDoubleClick) return@setOnClickListener
			shareApp(requireActivity().packageName, requireContext())
		}
		binding.btnFeedback.setOnClickListener {
			if (isDoubleClick) return@setOnClickListener
			sendFeedBack(requireContext())
		}
		binding.btnRate.setOnClickListener {
			if (isDoubleClick) return@setOnClickListener
			goToMarket(requireActivity().packageName, requireContext())
		}
		binding.btnPolicy.setOnClickListener {
			if (isDoubleClick) return@setOnClickListener

		}

		binding.btnChangePass.setOnClickListener {
			if (isDoubleClick) return@setOnClickListener
			TrackingHelper.logEvent(AllEvents.E1_CHANGE_PASS)
			val intent = Intent(requireContext(), UnlockActivity::class.java)
			intent.putExtra(UnlockActivity.KEY_TYPE_PASS, UnlockActivity.TYPE_CHANGE_PASS)
			startActivity(intent)
		}
	}

}