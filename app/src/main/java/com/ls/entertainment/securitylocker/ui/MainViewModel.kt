package com.ls.entertainment.securitylocker.ui

import androidx.lifecycle.viewModelScope
import com.entertainment.basemvvmproject.base.BaseViewModel
import com.entertainment.basemvvmproject.base.SingleLiveEvent
import com.ls.entertainment.securitylocker.utils.AllEvents
import com.ls.entertainment.securitylocker.utils.LogUtils
import com.ls.entertainment.securitylocker.utils.SharePreferenceUtils
import com.ls.entertainment.securitylocker.utils.TrackingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : BaseViewModel() {
	
	var stateSaveLockWallpaper = SingleLiveEvent<Boolean>()
	
	fun savePathImageLock(path: String) {
		viewModelScope.launch(Dispatchers.IO) {
			try {
				SharePreferenceUtils.getInstance().pathImageLock = path
				stateSaveLockWallpaper.postValue(true)
				TrackingHelper.logEvent(AllEvents.SET_WALLPAPER_LOCK_SUCCESS)
			} catch (e: Exception) {
				LogUtils.logCustomMessage(e.message.toString())
				TrackingHelper.logEvent(AllEvents.SET_WALLPAPER_LOCK_FAIL)
				stateSaveLockWallpaper.postValue(false)
			}
		}
	}
}
