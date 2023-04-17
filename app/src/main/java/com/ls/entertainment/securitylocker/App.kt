package com.ls.entertainment.securitylocker

import android.app.Application
import com.google.android.gms.ads.MobileAds

class App : Application() {
	override fun onCreate() {
		super.onCreate()
		MobileAds.initialize(this){

		}
	}

	companion object{
		var isShowLock = false
	}
}