package com.ls.entertainment.securitylocker.ui.detail

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.entertainment.basemvvmproject.base.BaseFragment
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.adapter.WallpaperAdapter
import com.ls.entertainment.securitylocker.adapter.WallpaperModel
import com.ls.entertainment.securitylocker.databinding.FragDetailBinding
import com.ls.entertainment.securitylocker.utils.AppConfig
import com.ls.entertainment.securitylocker.utils.AppSessionManager
import com.ls.entertainment.securitylocker.utils.setOnSafeClickListener

class DetailFragment : BaseFragment<FragDetailBinding, DetailViewModel>(R.layout.frag_detail) {

	private val viewModel: DetailViewModel by viewModels()

	var adapter: WallpaperAdapter? = null

	override fun getVM() = viewModel


	override fun bindingAction() {
		super.bindingAction()
		binding.container.setOnClickListener { }

		binding.ivBack.setOnSafeClickListener {
			requireActivity().onBackPressed()
		}

		binding.ivSetting.setOnSafeClickListener {
			if (adapter != null) {
				viewModel.savePathImageLock(adapter!!.listWallpaper[binding.viewPager.currentItem].url)
			}
		}
	}

	override fun bindingStateView() {
		super.bindingStateView()
		viewModel.stateSave.observe(viewLifecycleOwner) {
			if (it) showToast("Cài đặt màn hình khóa thành công") else showToast("Cài đặt màn hình khóa thất bại")
		}
	}

	override fun viewCreated(savedInstanceState: Bundle?) {
		super.viewCreated(savedInstanceState)
		initViewPager()
	}

	private fun initViewPager() {
		val ratio = AppConfig.aspectRatio
		val param = binding.viewPager.layoutParams
		param.height = (param.width * ratio).toInt()
		binding.viewPager.layoutParams = param
		adapter = WallpaperAdapter()
		adapter?.setData(AppSessionManager.listWallpaperDetail as ArrayList<WallpaperModel>)
		binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
		binding.viewPager.adapter = adapter
		binding.viewPager.setCurrentItem(requireArguments().getInt(KEY_CURRENT_POS, 0), false)
	}

	override fun onDestroy() {
		super.onDestroy()
		onClear()
	}

	companion object {
		private const val KEY_CURRENT_POS = "current_pos"

		fun newInstance(pos: Int, list: MutableList<WallpaperModel>): DetailFragment {
			AppSessionManager.listWallpaperDetail.clear()
			AppSessionManager.listWallpaperDetail.addAll(list)
			val fragment = DetailFragment()
			val bundle = Bundle()
			bundle.putInt(KEY_CURRENT_POS, pos)
			fragment.arguments = bundle
			return fragment
		}

		fun onClear() {
			AppSessionManager.listWallpaperDetail.clear()
		}
	}
}