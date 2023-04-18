package com.ls.entertainment.securitylocker.utils

import android.content.pm.ApplicationInfo

object AppUtils {
    fun isUserApp(applicationInfo: ApplicationInfo): Boolean {
        return applicationInfo.flags and 129 == 0
    }

    fun isSystemPackage(applicationInfo: ApplicationInfo): Boolean {
        return applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }
}