package com.ls.entertainment.securitylocker.ui.usage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.entertainment.basemvvmproject.base.BaseViewModel
import com.entertainment.basemvvmproject.base.SingleLiveEvent
import com.ls.entertainment.securitylocker.model.UsageTimeAppModel
import com.ls.entertainment.securitylocker.utils.LogUtils
import com.ls.entertainment.securitylocker.utils.PackageUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UsageViewModel : BaseViewModel() {
	var listUsageAppLiveData = MutableLiveData<MutableList<UsageTimeAppModel>>()

	var needLoading = SingleLiveEvent<Boolean>()

	fun loadUsageApp(type: Int) {
		viewModelScope.launch(Dispatchers.IO) {
			try {
				needLoading.postValue(true)
				delay(1000)
				var listTimeApp = mutableListOf<UsageTimeAppModel>()
				if (type == UsageFragment.TYPE_LAST_24_HOUR) {
					listTimeApp = PackageUtil.getTimeUserAppInstalled24Hour()
				} else if (type == UsageFragment.TYPE_LAST_10_DAY) {
					listTimeApp = PackageUtil.getTimeUserAppInstalledLast10Day()
				}
				listUsageAppLiveData.postValue(listTimeApp)
				needLoading.postValue(false)
			} catch (e: Exception) {
				needLoading.postValue(false)
				LogUtils.logCustomMessage(e.message.toString())
			}
		}
	}

	fun unInstallApp(packageName: String) {
		PackageUtil.uninstallPackage(packageName)
	}
}