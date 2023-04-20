package com.ls.entertainment.securitylocker.ui.app

import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.entertainment.basemvvmproject.base.BaseViewModel
import com.entertainment.basemvvmproject.base.SingleLiveEvent
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.model.AppModel
import com.ls.entertainment.securitylocker.utils.LogUtils
import com.ls.entertainment.securitylocker.utils.SharePreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class AppViewModel : BaseViewModel() {

	var listAppLiveData = MutableLiveData<MutableList<AppModel>>()

	var listPackageLock = mutableListOf<String>()

	var listResultSearch = MutableLiveData<MutableList<AppModel>>()

	var needLoading = SingleLiveEvent<Boolean>()

	init {
		getAllApp()
	}

	fun searchApp(keyword: String) {
		viewModelScope.launch(Dispatchers.IO) {
			val resultSearch = mutableListOf<AppModel>()
			val listApp = listAppLiveData.value
			listApp?.forEach {
				if (it.name.lowercase(Locale.ROOT).contains(keyword.lowercase(Locale.ROOT))) {
					resultSearch.add(it)
				}
			}
			listResultSearch.postValue(resultSearch)
		}
	}

	private suspend fun getListPackageLock() {
		listPackageLock = SharePreferenceUtils.getInstance().getListPackageLock()
	}

	fun updateListPackageLock(packageName: String, isLock: Boolean) {
		viewModelScope.launch(Dispatchers.IO) {
			if (isLock) {
				if (!listPackageLock.contains(packageName)) {
					listPackageLock.add(packageName)
				}
			} else {
				if (listPackageLock.contains(packageName)) {
					listPackageLock.remove(packageName)
				}
			}
			SharePreferenceUtils.getInstance()
				.setListPackageLock(listPackageLock as ArrayList<String>)
		}
	}

	private fun getAllApp() {
		viewModelScope.launch(Dispatchers.IO) {
			try {
				needLoading.postValue(true)
				getListPackageLock()
				val list = mutableListOf<AppModel>()
				val packageManager = App.instance.packageManager
				val appsData = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
				appsData.forEach {
					/* if (!AppUtils.isSystemPackage(it)) {
						 val appInformation =
							 App.instance.packageManager.getApplicationInfo(it.packageName, 0)
						 val appName = appInformation.loadLabel(App.instance.packageManager)
						 val drawableRes = appInformation.loadIcon(App.instance.packageManager)
						 list.add(
							 AppModel(
								 appName.toString(),
								 drawableRes,
								 it.packageName,
								 listPackageLock.contains(it.packageName)
							 )
						 )
					 }*/
					val appInformation =
						App.instance.packageManager.getApplicationInfo(it.packageName, 0)
					val appName = appInformation.loadLabel(App.instance.packageManager)
					val drawableRes = appInformation.loadIcon(App.instance.packageManager)
					list.add(
						AppModel(
							appName.toString(),
							drawableRes,
							it.packageName,
							if (listPackageLock.isEmpty()) false else listPackageLock.contains(it.packageName)
						)
					)
				}
				listAppLiveData.postValue(list)
				needLoading.postValue(false)
			} catch (e: Exception) {
				needLoading.postValue(false)
				LogUtils.logExceptionMessage(e.message.toString())
			}
		}
	}

}