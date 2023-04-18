package com.ls.entertainment.securitylocker

import android.annotation.SuppressLint
import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        initAdmobAd()
    }

    private fun initAdmobAd() {
        MobileAds.initialize(this){

        }
    }

    companion object{
        var isShowLock = false

        @SuppressLint("StaticFieldLeak")
        lateinit var instance: App
            private set
    }
}