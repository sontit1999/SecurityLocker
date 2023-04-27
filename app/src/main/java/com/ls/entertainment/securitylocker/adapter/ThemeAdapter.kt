package com.ls.entertainment.securitylocker.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.entertainment.basemvvmproject.utils.gone
import com.entertainment.basemvvmproject.utils.loadImage
import com.entertainment.basemvvmproject.utils.visible
import com.ls.entertainment.securitylocker.ads.AdManager
import com.ls.entertainment.securitylocker.databinding.ItemNativeAdBinding
import com.ls.entertainment.securitylocker.databinding.ItemThemelBinding
import com.ls.entertainment.securitylocker.model.AdsModel
import com.ls.entertainment.securitylocker.utils.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference


class ThemeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	private var listIdol = ArrayList<WallpaperModel>()
	private var weakRecyclerView: WeakReference<RecyclerView>? = null
	var screenType = ScreenType.COLLECTION
	var onClickItem: ((Int, WallpaperModel) -> Unit)? = null
	var composeDisposable = CompositeDisposable()
	var adsModel: AdsModel? = null
	private var isLoadingAd = false
	private var disposableAds: Disposable? = null
	private var loadedNativeAdCount = 0
	lateinit var layoutManager: GridLayoutManager

	fun getData() = listIdol

	@SuppressLint("NotifyDataSetChanged")
	fun setData(listIdol: MutableList<WallpaperModel>) {
		this.listIdol.clear()
		this.listIdol.addAll(buildListData(listIdol))
		notifyDataSetChanged()
	}

	private fun buildListData(list: List<WallpaperModel>): List<WallpaperModel> {
		val listData = mutableListOf<WallpaperModel>()
		var posAddNativeStart = RemoteConfig.commonConfig.posAddNativeStart
		list.forEachIndexed { index, pictureModel ->
			if (index == posAddNativeStart) {
				listData.add(pictureModel)
				listData.add(WallpaperModel(1, NAME_NATIVE_ADS, "", false))
				posAddNativeStart += RemoteConfig.commonConfig.distanceNativeAd
			} else {
				listData.add(pictureModel)
			}
		}
		return listData
	}


	private val canLoadAds: Boolean get() = adsModel == null || adsModel!!.count > RemoteConfig.commonConfig.numberOfNativeDisplay

	fun loadAds() {
		if (isLoadingAd || !canLoadAds || !RemoteConfig.commonConfig.supportNative || !RemoteConfig.commonConfig.isActiveAds) return
		LogUtils.logCustomMessage("load new NativeAds")
		isLoadingAd = true
		loadedNativeAdCount++
		disposableAds?.dispose()
		disposableAds = AdManager.loadNativeAd().doFinally {
			isLoadingAd = false
			//Nếu tải thất bại thì reload ads 2 lần
			if (loadedNativeAdCount < 3 && adsModel == null) {
				loadAds()
			}
		}.subscribe({
			val model = adsModel
			adsModel = AdsModel(it, 0)
			// notify update native ads
			notifyItemNativeAds()
			model?.nativeAd?.destroy()
			LogUtils.logCustomMessage("load native in list success")

		}, {
			LogUtils.logCustomMessage("load native in list error")
		})
	}

	private val onScroll = object : RecyclerView.OnScrollListener() {
		override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

		}

		override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
			if (newState == RecyclerView.SCROLL_STATE_IDLE) {
				loadAds()
			}
		}
	}

	private fun notifyItemNativeAds() {
		try {
			weakRecyclerView?.get()?.let {
				val layoutManager = it.layoutManager as LinearLayoutManager
				layoutManager.let {
					val first = layoutManager.findFirstVisibleItemPosition()
					val last = layoutManager.findLastVisibleItemPosition()
					for (i in first..last) {
						if (getItemViewType(i) == TYPE_ADS) {
							notifyItemChanged(i)
							LogUtils.logCustomMessage("Notify item native after load ads = $i")
						}
					}
				}
			}

		} catch (e: Exception) {
			LogUtils.logCustomMessage(e.message.toString())
		}
	}

	override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
		super.onAttachedToRecyclerView(recyclerView)
		weakRecyclerView = WeakReference(recyclerView)
		layoutManager = recyclerView.layoutManager as GridLayoutManager
		layoutManager.spanSizeLookup = object : SpanSizeLookup() {
			override fun getSpanSize(position: Int): Int {
				return if (getItemViewType(position) == TYPE_ITEM) {
					1
				} else 2
			}
		}
		recyclerView.addOnScrollListener(onScroll)
		val animator = recyclerView.itemAnimator
		if (animator is SimpleItemAnimator) {
			animator.supportsChangeAnimations = false
		}
	}

	private fun getRecyclerView() = weakRecyclerView?.get()

	fun addData(listIdol: List<WallpaperModel>) {
		val oldSize = this.listIdol.size
		this.listIdol.addAll(buildListData(listIdol))
		notifyItemRangeInserted(oldSize, listIdol.size)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

		return when (viewType) {
			TYPE_ITEM -> IdolHolder(
				ItemThemelBinding.inflate(
					LayoutInflater.from(parent.context), parent, false
				)
			)

			else      -> NativeAdsHolder(
				ItemNativeAdBinding.inflate(
					LayoutInflater.from(parent.context), parent, false
				)
			)
		}
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

		if (holder is IdolHolder) {
			try {
				val pictureModel = listIdol[position]
				holder.binData(pictureModel)
			} catch (e: Exception) {
				holder.binding.ivWallpaper.visibility = View.GONE
			}
		}

		if (holder is NativeAdsHolder) {
			holder.binData(adsModel)
		}
	}

	private fun isGridLayoutManagerInitialized(): Boolean {
		return this::layoutManager.isInitialized
	}

	fun showOrHideAd(isHidden: Boolean) {
		if (!isGridLayoutManagerInitialized()) return
		val start: Int = layoutManager.findFirstVisibleItemPosition()
		val end = layoutManager.findLastVisibleItemPosition()
		for (i in start..end) {
			val vh: RecyclerView.ViewHolder? =
				weakRecyclerView?.get()?.findViewHolderForAdapterPosition(i)
			vh?.let {
				if (vh is NativeAdsHolder) {
					if (isHidden) {
						vh.itemView.visibility = View.INVISIBLE
					} else vh.itemView.visibility = View.VISIBLE
				}
			}
		}
	}


	override fun getItemCount() = listIdol.size

	override fun getItemViewType(position: Int): Int {
		return if (listIdol[position].name == NAME_NATIVE_ADS) TYPE_ADS else TYPE_ITEM
	}

	fun release() {
		weakRecyclerView?.clear()
		weakRecyclerView = null
		listIdol.clear()
		composeDisposable.clear()
		disposableAds?.dispose()
	}

	inner class IdolHolder(var binding: ItemThemelBinding) : RecyclerView.ViewHolder(binding.root) {

		init {
			itemView.setOnSafeClickListener {
				onClickItem?.invoke(
					adapterPosition, listIdol[adapterPosition]
				)
			}
		}

		fun binData(wall: WallpaperModel) {
			binding.cardView.setRandomBackgroundColor()
			binding.ivWallpaper.loadImage(wall.url)
		}
	}

	inner class NativeAdsHolder(
		val binding: ItemNativeAdBinding,
	) : RecyclerView.ViewHolder(binding.root) {

		fun binData(adsModel: AdsModel?) {
			Log.d("haha", "bin Data native ads")
			if (adsModel == null) {
				binding.nativeLayout.gone()
				return
			}
			adsModel.count++
			binding.nativeLayout.visible()
			binding.nativeLayout.binDataNativeAds(adsModel.nativeAd)
		}

	}

	companion object {
		const val TYPE_ITEM = 0
		const val TYPE_ADS = 1
		const val NAME_NATIVE_ADS = "native"
	}

}