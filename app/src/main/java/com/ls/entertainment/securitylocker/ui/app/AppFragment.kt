package com.ls.entertainment.securitylocker.ui.app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.entertainment.basemvvmproject.base.BaseFragment
import com.entertainment.basemvvmproject.utils.gone
import com.entertainment.basemvvmproject.utils.visible
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.adapter.AppAdapter
import com.ls.entertainment.securitylocker.databinding.FragAppBinding
import com.ls.entertainment.securitylocker.extension.isAllowAllPermission
import com.ls.entertainment.securitylocker.model.CheckPermissionEvent
import com.ls.entertainment.securitylocker.utils.AllEvents
import com.ls.entertainment.securitylocker.utils.AppUtils.goDetailInformationApp
import com.ls.entertainment.securitylocker.utils.TrackingHelper
import org.greenrobot.eventbus.EventBus

class AppFragment : BaseFragment<FragAppBinding, AppViewModel>(R.layout.frag_app) {

	private val viewModel: AppViewModel by viewModels()
	lateinit var adapterApp: AppAdapter
	private var handleSearch = Handler(Looper.getMainLooper())
	private var keyword = ""
	private var runnableSearch = Runnable {
		if (!requireContext().isAllowAllPermission()) {
			EventBus.getDefault().post(CheckPermissionEvent())
		}
		viewModel.searchApp(keyword)
	}

	override fun getVM() = viewModel

	override fun bindingStateView() {
		super.bindingStateView()
		viewModel.listAppLiveData.observe(viewLifecycleOwner) {
			adapterApp.setData(it)
		}

		viewModel.listResultSearch.observe(viewLifecycleOwner) {
			adapterApp.setData(it)
		}

		viewModel.needLoading.observe(viewLifecycleOwner) {
			if (it) binding.pbLoading.visible() else binding.pbLoading.gone()
		}
	}

	override fun bindingAction() {
		super.bindingAction()
		binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
			override fun onQueryTextSubmit(query: String?): Boolean {
				keyword = query ?: ""
				handleSearch.removeCallbacks(runnableSearch)
				handleSearch.postDelayed(runnableSearch, 500)
				return true
			}

			override fun onQueryTextChange(newText: String?): Boolean {
				keyword = newText ?: ""
				handleSearch.removeCallbacks(runnableSearch)
				handleSearch.postDelayed(runnableSearch, 500)
				return true
			}
		})
	}
	
	override fun viewCreated(savedInstanceState: Bundle?) {
		super.viewCreated(savedInstanceState)
		initRvApp()
	}
	
	override fun onDestroy() {
		super.onDestroy()
		handleSearch.removeCallbacks(runnableSearch)
	}
	
	override fun onResume() {
		super.onResume()
		TrackingHelper.logEvent(AllEvents.VIEW_ALL_APP)
	}
	
	private fun initRvApp() {
		adapterApp = AppAdapter()
		adapterApp.onClickLock = {
			TrackingHelper.logEvent(AllEvents.ACTION_LOCK)
			viewModel.updateListPackageLock(it.packageName, it.isLock)
		}
		adapterApp.onClickItem = {
			TrackingHelper.logEvent(AllEvents.ACTION_INFORMATION)
			goDetailInformationApp(requireContext(), it.packageName)
		}
		binding.rvApp.itemAnimator = null
		binding.rvApp.layoutManager =
			LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
		binding.rvApp.adapter = adapterApp
	}
}