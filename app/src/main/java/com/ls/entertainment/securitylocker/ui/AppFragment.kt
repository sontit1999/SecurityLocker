package com.ls.entertainment.securitylocker.ui

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.entertainment.basemvvmproject.base.BaseFragment
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.adapter.AppAdapter
import com.ls.entertainment.securitylocker.databinding.FragAppBinding

class AppFragment : BaseFragment<FragAppBinding, AppViewModel>(R.layout.frag_app) {

    private val viewModel: AppViewModel by viewModels()
    lateinit var adapterApp: AppAdapter

    override fun getVM() = viewModel

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.listAppLiveData.observe(viewLifecycleOwner) {
            adapterApp.setData(it)
        }
    }

    override fun viewCreated(savedInstanceState: Bundle?) {
        super.viewCreated(savedInstanceState)
        initRvApp()
    }

    private fun initRvApp() {
        adapterApp = AppAdapter()
        adapterApp.onClickItem = {
            viewModel.updateListPackageLock(it.packageName, it.isLock)
        }
        binding.rvApp.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvApp.adapter = adapterApp
    }
}