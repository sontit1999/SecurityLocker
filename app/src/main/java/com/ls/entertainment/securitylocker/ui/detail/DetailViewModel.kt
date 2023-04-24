package com.ls.entertainment.securitylocker.ui.detail

import androidx.lifecycle.viewModelScope
import com.entertainment.basemvvmproject.base.BaseViewModel
import com.entertainment.basemvvmproject.base.SingleLiveEvent
import com.ls.entertainment.securitylocker.utils.SharePreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewModel : BaseViewModel() {

	var stateSave = SingleLiveEvent<Boolean>()

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
}