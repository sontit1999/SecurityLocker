package com.ls.entertainment.securitylocker.utils

import android.content.Context
import android.content.SharedPreferences
import com.ls.entertainment.securitylocker.App


class SharePreferenceUtils constructor(context: Context) {
    private val editor: SharedPreferences.Editor
    private val pre: SharedPreferences

    var openCount: Int
        get() = pre.getInt("openCount", 0)
        set(j) {
            editor.putInt("openCount", j)
            editor.commit()
        }


    var lastTimeOpenApp: Long
        get() = pre.getLong("lastTimeOpenApp", 0)
        set(i) {
            editor.putLong("lastTimeOpenApp", i)
            editor.commit()
        }

    var indexNotification: Int
        get() = pre.getInt("indexNotification", 0)
        set(i) {
            editor.putInt("indexNotification", i)
            editor.commit()
        }

    var enableNotification: Boolean
        get() = pre.getBoolean("enableNotification", true)
        set(i) {
            editor.putBoolean("enableNotification", i)
            editor.commit()
        }

    var isSetupLanguage: Boolean
        get() = pre.getBoolean("isSetupLanguage", false)
        set(i) {
            editor.putBoolean("isSetupLanguage", i)
            editor.commit()
        }

    var canShowOpenAd: Boolean
        get() = pre.getBoolean("canShowOpenAd", false)
        set(i) {
            editor.putBoolean("canShowOpenAd", i)
            editor.commit()
        }

    var lastPackageLock: String?
        get() = pre.getString("lastPackageLock", null)
        set(i) {
            editor.putString("lastPackageLock", i)
            editor.commit()
        }


    init {
        val sharedPreferences = context.getSharedPreferences(
            "app_data", Context.MODE_PRIVATE
        )
        pre = sharedPreferences
        editor = sharedPreferences.edit()
    }

    companion object {

        var instance: SharePreferenceUtils? = null

        @JvmName("getInstance1")
        fun getInstance(): SharePreferenceUtils {
            if (instance == null) instance = SharePreferenceUtils(App.instance)
            return instance!!
        }
    }
}