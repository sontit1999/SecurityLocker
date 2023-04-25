package com.ls.entertainment.securitylocker.adapter

import androidx.recyclerview.widget.RecyclerView
import com.entertainment.basemvvmproject.utils.gone
import com.entertainment.basemvvmproject.utils.loadImage
import com.entertainment.basemvvmproject.utils.visible
import com.ls.entertainment.securitylocker.databinding.ItemWallpaperDetailBinding

class WallpaperHolder(var binding: ItemWallpaperDetailBinding) :
	RecyclerView.ViewHolder(binding.root) {
	private var typePreview = TYPE_PREVIEW_PATTERN

	init {
		if (typePreview == TYPE_PREVIEW_PATTERN) {
			binding.patternLockView.visible()
			binding.indicatorDots.gone()
			binding.pinLockView.gone()
		} else {
			binding.patternLockView.gone()
			binding.indicatorDots.visible()
			binding.pinLockView.visible()
		}
	}

	fun changePreview() {
		if (typePreview == TYPE_PREVIEW_PATTERN) {
			typePreview = TYPE_PREVIEW_PIN
			binding.patternLockView.gone()
			binding.indicatorDots.visible()
			binding.pinLockView.visible()
		} else {
			typePreview = TYPE_PREVIEW_PATTERN
			binding.patternLockView.visible()
			binding.indicatorDots.gone()
			binding.pinLockView.gone()
		}
	}

	fun binDataWall(wallpaperModel: WallpaperModel) {
		binding.ivWallpaper.loadImage(wallpaperModel.url)
	}

	companion object {
		const val TYPE_PREVIEW_PATTERN = 32
		const val TYPE_PREVIEW_PIN = 43
	}
}