package com.ls.entertainment.securitylocker.ui.tool

import androidx.fragment.app.viewModels
import com.entertainment.basemvvmproject.base.BaseFragment
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.databinding.FragToolBinding

class ToolFragment : BaseFragment<FragToolBinding, ToolViewModel>(R.layout.frag_tool) {
	
	private val viewModel: ToolViewModel by viewModels()
	
	override fun getVM() = viewModel
	
}