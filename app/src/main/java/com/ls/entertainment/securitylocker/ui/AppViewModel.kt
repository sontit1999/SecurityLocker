package com.ls.entertainment.securitylocker.ui

import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.entertainment.basemvvmproject.base.BaseViewModel
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.model.AppModel
import com.ls.entertainment.securitylocker.utils.AppUtils
import com.ls.entertainment.securitylocker.utils.SharePreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppViewModel : BaseViewModel() {

    var listAppLiveData = MutableLiveData<MutableList<AppModel>>()

    var listPackageLock = mutableListOf<String>()

    init {

        getAllApp()
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
                getListPackageLock()
                val list = mutableListOf<AppModel>()
                val packageManager = App.instance.packageManager
                val appsData = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                appsData.forEach {
                    if (!AppUtils.isSystemPackage(it)) {
                        val appInfor =
                            App.instance.packageManager.getApplicationInfo(it.packageName, 0)
                        val appName = appInfor.loadLabel(App.instance.packageManager)
                        val drawableRes = appInfor.loadIcon(App.instance.packageManager)
                        list.add(
                            AppModel(
                                appName.toString(),
                                drawableRes,
                                it.packageName,
                                listPackageLock.contains(it.packageName)
                            )
                        )
                    }
                }
                listAppLiveData.postValue(list)
            } catch (e: Exception) {

            }
        }
    }

}