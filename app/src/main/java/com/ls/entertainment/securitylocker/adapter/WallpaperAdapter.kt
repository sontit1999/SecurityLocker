package com.ls.entertainment.securitylocker.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ls.entertainment.securitylocker.databinding.ItemWallpaperDetailBinding
import com.ls.entertainment.securitylocker.utils.AllEvents
import com.ls.entertainment.securitylocker.utils.TrackingHelper

class WallpaperAdapter : RecyclerView.Adapter<WallpaperHolder>() {
	
	var listWallpaper = ArrayList<WallpaperModel>()
	
	fun getCountItem() = listWallpaper.size
	var onClickItem: ((WallpaperModel) -> Unit)? = null
	
	@SuppressLint("NotifyDataSetChanged")
	fun setData(listWallpaper: ArrayList<WallpaperModel>) {
		this.listWallpaper = listWallpaper
		notifyDataSetChanged()
	}
	
	fun addData(listWallpaper: List<WallpaperModel>) {
		val oldSize = this.listWallpaper.size
		this.listWallpaper.addAll(listWallpaper)
		notifyItemRangeInserted(oldSize, listWallpaper.size)
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperHolder {
		return WallpaperHolder(
			ItemWallpaperDetailBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	}
	
	override fun onBindViewHolder(holder: WallpaperHolder, position: Int) {
		holder.binDataWall(listWallpaper[position])
		holder.binding.ivWallpaper.setOnClickListener {
			onClickItem?.invoke(listWallpaper[position])
			holder.changePreview()
			TrackingHelper.logEvent(AllEvents.ACTION_CHANGE_PREVIEW)
		}
		holder.binding.pinLockView.attachIndicatorDots(holder.binding.indicatorDots)
	}

	override fun getItemCount() = listWallpaper.size
}