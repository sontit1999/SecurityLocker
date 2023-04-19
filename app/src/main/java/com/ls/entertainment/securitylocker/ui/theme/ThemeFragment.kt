package com.ls.entertainment.securitylocker.ui.theme

import androidx.fragment.app.viewModels
import com.entertainment.basemvvmproject.base.BaseFragment
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.databinding.FragThemeBinding

class ThemeFragment : BaseFragment<FragThemeBinding, ThemeViewModel>(R.layout.frag_theme) {
	
	private val viewModel: ThemeViewModel by viewModels()
	
	override fun getVM() = viewModel
	
}