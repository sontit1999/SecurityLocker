package com.ls.entertainment.securitylocker

import android.annotation.SuppressLint
import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.ls.entertainment.securitylocker.utils.AppConfig
import com.ls.entertainment.securitylocker.utils.NotificationCenter
import com.ls.entertainment.securitylocker.utils.WallpaperUtils
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        AppConfig.setup(this)
        NotificationCenter.createChannelNotification()
        initAdmobAd()
    }

    private fun initAdmobAd() {
        MobileAds.initialize(this){

        }
    }

    companion object{
        var isShowLock = false
        var didLoadConfigSuccess = false
        var didOptimizeBatterySaver = false
		var brightnessValue = 0
		var typeSetWallpaper = WallpaperUtils.WallpaperType.LOCK_APP

        @SuppressLint("StaticFieldLeak")
        lateinit var instance: App
            private set
    }
}