package com.ls.entertainment.securitylocker.ui

import androidx.lifecycle.viewModelScope
import com.entertainment.basemvvmproject.base.BaseViewModel
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.utils.LogUtils
import com.ls.entertainment.securitylocker.utils.SharePreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : BaseViewModel() {
	fun savePathImageLock(path: String) {
		viewModelScope.launch(Dispatchers.IO) {
			try {
				SharePreferenceUtils.getInstance().pathImageLock = path
				toastMessage.postValue(App.instance.getString(R.string.msg_set_background_ok))
			} catch (e: Exception) {
				LogUtils.logCustomMessage(e.message.toString())
				toastMessage.postValue(App.instance.getString(R.string.msg_set_background_fail))
			}
		}
	}
}
