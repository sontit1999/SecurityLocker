package com.ls.entertainment.securitylocker.ui.usage

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.entertainment.basemvvmproject.base.BaseFragment
import com.entertainment.basemvvmproject.utils.gone
import com.entertainment.basemvvmproject.utils.visible
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.adapter.UsageAppAdapter
import com.ls.entertainment.securitylocker.databinding.FragUsageBinding
import com.ls.entertainment.securitylocker.model.RefreshUsage
import com.ls.entertainment.securitylocker.utils.AllEvents
import com.ls.entertainment.securitylocker.utils.TrackingHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class UsageFragment : BaseFragment<FragUsageBinding, UsageViewModel>(R.layout.frag_usage) {

	private val viewModel: UsageViewModel by viewModels()
	private lateinit var usageAppAdapter: UsageAppAdapter
	private var typeGetUsage = -1

	override fun getVM() = viewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		EventBus.getDefault().register(this)
		typeGetUsage = arguments?.getInt(KEY_TYPE_USAGE, TYPE_LAST_24_HOUR)!!
	}

	override fun viewCreated(savedInstanceState: Bundle?) {
		super.viewCreated(savedInstanceState)
		setUpRvUsageApp()
	}


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewModel.loadUsageApp(typeGetUsage)
	}

	override fun bindingStateView() {
		super.bindingStateView()
		viewModel.listUsageAppLiveData.observe(viewLifecycleOwner) {
			usageAppAdapter.setData(it)
		}
		viewModel.needLoading.observe(viewLifecycleOwner) {
			if (it) binding.pbLoading.visible() else binding.pbLoading.gone()
		}
	}

	private fun setUpRvUsageApp() {
		binding.rvApp.setHasFixedSize(true)
		binding.rvApp.layoutManager =
			LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
		usageAppAdapter = UsageAppAdapter()
		usageAppAdapter.onClickItem = {
			TrackingHelper.logEvent(AllEvents.ACTION_UNINSTALL)
			viewModel.unInstallApp(it.packageName)
		}
		binding.rvApp.adapter = usageAppAdapter
	}
	
	@Subscribe
	fun refreshEvent(refreshUsage: RefreshUsage) {
		viewModel.loadUsageApp(typeGetUsage)
	}
	
	override fun onResume() {
		super.onResume()
		EventBus.getDefault().post(RefreshUsage())
	}
	
	override fun onDestroy() {
		super.onDestroy()
		EventBus.getDefault().unregister(this)
	}
	
	companion object {
		private const val KEY_TYPE_USAGE = "KEY_TYPE_USAGE"
		const val TYPE_LAST_24_HOUR = 0
		const val TYPE_LAST_10_DAY = 1

		fun newInstance(typeTimeUsage: Int): UsageFragment {
			val fragment = UsageFragment()
			fragment.arguments = bundleOf(KEY_TYPE_USAGE to typeTimeUsage)
			return fragment
		}
	}
}