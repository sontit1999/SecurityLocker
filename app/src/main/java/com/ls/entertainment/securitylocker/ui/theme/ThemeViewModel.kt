package com.ls.entertainment.securitylocker.ui.theme

import androidx.lifecycle.viewModelScope
import com.entertainment.basemvvmproject.base.BaseViewModel
import com.ls.entertainment.securitylocker.adapter.ThemeAdapter
import com.ls.entertainment.securitylocker.utils.FileUtils

import kotlinx.coroutines.launch

class ThemeViewModel : BaseViewModel() {
	
	var adapter = ThemeAdapter()
	fun getImageLocal() {
		viewModelScope.launch {
			try {
				isLoading.postValue(true)
				val listImage = FileUtils.localStorageQuery()
				adapter.setData(listImage)
				isLoading.postValue(false)
			} catch (e: Exception) {
				isLoading.postValue(false)
				toastMessage.postValue(e.message)
			}
		}
	}
}