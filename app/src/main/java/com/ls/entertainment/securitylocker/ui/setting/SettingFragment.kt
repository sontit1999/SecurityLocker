package com.ls.entertainment.securitylocker.ui.setting

import androidx.fragment.app.viewModels
import com.entertainment.basemvvmproject.base.BaseFragment
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.databinding.FragToolBinding

class SettingFragment : BaseFragment<FragToolBinding, SettingViewModel>(R.layout.frag_setting) {
	
	private val viewModel: SettingViewModel by viewModels()
	
	override fun getVM() = viewModel
	
}