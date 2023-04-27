package com.ls.entertainment.securitylocker.ui.detail

import androidx.lifecycle.viewModelScope
import com.entertainment.basemvvmproject.base.BaseViewModel
import com.entertainment.basemvvmproject.base.SingleLiveEvent
import com.ls.entertainment.securitylocker.di.ApiInterface
import com.ls.entertainment.securitylocker.utils.Constant
import com.ls.entertainment.securitylocker.utils.FileUtils
import com.ls.entertainment.securitylocker.utils.SharePreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewModel : BaseViewModel() {

	var stateSave = SingleLiveEvent<Boolean>()

	private lateinit var apiInterface: ApiInterface

	var pathImageToSetWallpaper = ""

	var stateDownloadImage = SingleLiveEvent<Boolean>()

	fun init(apiInterface: ApiInterface) {
		this.apiInterface = apiInterface
	}

	fun savePathImageLock(path: String) {
		viewModelScope.launch(Dispatchers.IO) {
			try {
				SharePreferenceUtils.getInstance().pathImageLock = path
				stateSave.postValue(true)
			} catch (e: Exception) {
				stateSave.postValue(false)
			}
		}
	}


	fun downLoadImage(
		url: String?,
	) {
		viewModelScope.launch(Dispatchers.IO) {
			try {
				isLoading.postValue(true)
				if (url.isNullOrEmpty()) {
					stateDownloadImage.postValue(false)
					return@launch
				}
				val responseBody = apiInterface.downloadImageSuspend(url)
				val status = FileUtils.writeToDisk(responseBody, Constant.nameFolderDownloadImage)
				pathImageToSetWallpaper = status.second ?: ""
				//FileUtils.updateFileToGallery(status.second ?: "")
				stateDownloadImage.postValue(true)
				isLoading.postValue(false)
			} catch (e: Exception) {
				stateDownloadImage.postValue(false)
				isLoading.postValue(false)
			}
		}
	}


}