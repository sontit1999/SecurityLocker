package com.ls.entertainment.securitylocker.adapter

import androidx.recyclerview.widget.RecyclerView
import com.entertainment.basemvvmproject.utils.loadImage
import com.ls.entertainment.securitylocker.databinding.ItemWallpaperDetailBinding

class WallpaperHolder(var binding: ItemWallpaperDetailBinding) :
	RecyclerView.ViewHolder(binding.root) {
	
	init {
		binding.ivWallpaper.setOnClickListener { }
	}
	
	fun binDataWall(wallpaperModel: WallpaperModel) {
		binding.ivWallpaper.loadImage(wallpaperModel.url)
	}
}