package com.ls.entertainment.securitylocker.ui.detail

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.entertainment.basemvvmproject.base.BaseFragment
import com.entertainment.basemvvmproject.utils.DeviceUtil
import com.entertainment.basemvvmproject.utils.gone
import com.entertainment.basemvvmproject.utils.visible
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.adapter.WallpaperAdapter
import com.ls.entertainment.securitylocker.adapter.WallpaperModel
import com.ls.entertainment.securitylocker.ads.AdManager
import com.ls.entertainment.securitylocker.databinding.FragDetailBinding
import com.ls.entertainment.securitylocker.di.ApiInterface
import com.ls.entertainment.securitylocker.model.OpenAdEvent
import com.ls.entertainment.securitylocker.model.ShowInterAfterSetSuccessEvent
import com.ls.entertainment.securitylocker.utils.*
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : BaseFragment<FragDetailBinding, DetailViewModel>(R.layout.frag_detail) {
	
	private val viewModel: DetailViewModel by viewModels()
	
	var adapter: WallpaperAdapter? = null
	
	@Inject
	lateinit var apiInterface: ApiInterface
	
	override fun getVM() = viewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		EventBus.getDefault().register(this)
	}
	
	
	override fun bindingAction() {
		super.bindingAction()
		binding.container.setOnClickListener { }
		
		binding.ivBack.setOnSafeClickListener {
			TrackingHelper.logEvent(AllEvents.ACTION_BACK_DETAIL)
			if (!AdManager.showInter(false, TAG, onHidden = {
					requireActivity().onBackPressed()
				})) {
				requireActivity().onBackPressed()
			}
		}
		
		binding.ivSetting.setOnSafeClickListener {
			TrackingHelper.logEvent(AllEvents.ACTION_DOWNLOAD_WALLPAPER)
			if (NetworkListener.isNetWorkConnected()) {
				if (adapter?.listWallpaper?.get(binding.viewPager.currentItem)?.isFromLocal == false) {
					DialogUtil.showConfirmationWatchAdDialog(requireContext(), okListener = {
						if (!AdManager.showRewarded(onHidden = {
								viewModel.downLoadImage(
									adapter?.listWallpaper?.get(binding.viewPager.currentItem)?.url
										?: ""
								)
							})) {
							viewModel.downLoadImage(
								adapter?.listWallpaper?.get(binding.viewPager.currentItem)?.url
									?: ""
							)
						}
					})
				} else {
					viewModel.pathImageToSetWallpaper =
						adapter?.listWallpaper?.get(binding.viewPager.currentItem)?.url.toString()
					showDialogSettingBackground()
				}
			} else {
				DialogUtil.showConfirmationNetworkDialog(requireContext())
			}
		}
	}
	
	private fun showDialogSettingBackground() {
		DialogUtil.showSetWallpaperDialog(requireContext(), lockAppListener = {
			goCropActivityAndSet(WallpaperUtils.WallpaperType.LOCK_APP)
			TrackingHelper.logEvent(AllEvents.ACTION_SET_LOCK_APP)
		}, lockHomeListener = {
			TrackingHelper.logEvent(AllEvents.ACTION_SET_HOME_SCREEN)
			goCropActivityAndSet(WallpaperUtils.WallpaperType.HOME)
		}, lockListener = {
			TrackingHelper.logEvent(AllEvents.ACTION_SET_LOCK_SCREEN)
			goCropActivityAndSet(WallpaperUtils.WallpaperType.LOCK)
		})
	}
	
	private fun goCropActivityAndSet(type: WallpaperUtils.WallpaperType) {
		App.typeSetWallpaper = type
		val fileLocal = viewModel.pathImageToSetWallpaper
		
		UCrop.of(Uri.fromFile(File(fileLocal)), Uri.fromFile(File.createTempFile("temp", ".png")))
			.withAspectRatio(
				DeviceUtil.getWidthScreen(requireActivity()).toFloat(),
				DeviceUtil.getScreenHeight(requireActivity()).toFloat()
			).start(requireActivity())
	}
	
	override fun bindingStateView() {
		super.bindingStateView()
		viewModel.stateSave.observe(viewLifecycleOwner) {
			if (it) showToast(getString(R.string.msg_set_background_ok)) else showToast(getString(R.string.msg_set_background_fail))
		}
		viewModel.stateDownloadImage.observe(viewLifecycleOwner) {
			if (it) {
				showDialogSettingBackground()
			} else {
				showToast(getString(R.string.download_image_fail))
			}
		}
		
		RxBus.subscribe(TAG, OpenAdEvent::class) {
			if (it.isShow) {
				binding.containerAds.gone()
			} else binding.containerAds.visible()
		}
	}
	
	override fun viewCreated(savedInstanceState: Bundle?) {
		super.viewCreated(savedInstanceState)
		viewModel.init(apiInterface)
		initViewPager()
		AdManager.loadBanner(binding.containerAds, AppConstant.BANNER_DETAIL_KEY,RemoteConfig.commonConfig.supportBannerCollapseAllScreen)
	}
	
	private fun initViewPager() {
		val ratio = AppConfig.aspectRatio
		val param = binding.viewPager.layoutParams
		param.height = (param.width * ratio).toInt()
		binding.viewPager.layoutParams = param
		adapter = WallpaperAdapter()
		adapter?.setData(AppSessionManager.listWallpaperDetail as ArrayList<WallpaperModel>)
		binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
		binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
			override fun onPageSelected(position: Int) {
				super.onPageSelected(position)
				TrackingHelper.logEvent(AllEvents.ACTION_SWIPE_DETAIL)
			}
		})
		binding.viewPager.adapter = adapter
		binding.viewPager.setCurrentItem(requireArguments().getInt(KEY_CURRENT_POS, 0), false)
	}
	
	override fun onResume() {
		super.onResume()
		TrackingHelper.logEvent(AllEvents.VIEW_DETAIL)
	}

	@Subscribe
	fun showInterAfterSetSuccessEvent(showInterAfterSetSuccessEvent: ShowInterAfterSetSuccessEvent){
		DialogUtil.showCongratulationDialog(requireContext(), OkeListener = {
			if (!AdManager.showInter(false, TAG, onHidden = {
					requireActivity().onBackPressed()
				})) {
				requireActivity().onBackPressed()
			}
		})
	}
	
	override fun onDestroy() {
		super.onDestroy()
		LogUtils.logCustomMessage("================ onDestroy Detail")
		RxBus.unregister(TAG)
		EventBus.getDefault().unregister(this)
		onClear()
	}
	
	companion object {
		private const val KEY_CURRENT_POS = "current_pos"
		const val TAG = "DetailFragment"
		
		fun newInstance(
			pos: Int,
			list: MutableList<WallpaperModel>,
			item: WallpaperModel
		): DetailFragment {
			AppSessionManager.listWallpaperDetail.clear()
			AppSessionManager.listWallpaperDetail.addAll(AppUtils.removeNativeItem(list))
			var currentPos = 0
			AppSessionManager.listWallpaperDetail.forEachIndexed { index, wallpaperModel ->
				if (item.url == wallpaperModel.url) {
					currentPos = index
				}
			}
			val fragment = DetailFragment()
			val bundle = Bundle()
			bundle.putInt(KEY_CURRENT_POS, currentPos)
			fragment.arguments = bundle
			return fragment
		}
		
		fun onClear() {
			AppSessionManager.listWallpaperDetail.clear()
		}
	}
}