package com.ls.entertainment.securitylocker.utils

import android.util.Log

object LogUtils {
    fun logCustomMessage(msg : String){
        Log.d("ahihi",msg)
    }

    fun logCustomMessage(tag : String,msg : String){
        Log.d(tag,msg)
    }

    fun logExceptionMessage(msg : String){
        Log.e("ahihi",msg)
    }
}