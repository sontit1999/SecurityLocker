package com.ls.entertainment.securitylocker.ui.batterysaver

import androidx.lifecycle.MutableLiveData
import com.entertainment.basemvvmproject.base.BaseViewModel
import com.entertainment.basemvvmproject.base.SingleLiveEvent
import com.google.android.gms.ads.nativead.NativeAd

class BatterySaverViewModel : BaseViewModel() {

	var stateOptimize: SingleLiveEvent<Boolean> = SingleLiveEvent()

	var nativeAdsLiveData = MutableLiveData<NativeAd>()

	fun loadNativeAds() {

	}

}