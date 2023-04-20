package com.ls.entertainment.securitylocker.utils

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.BuildConfig

object TrackingHelper {
	private var mFirebaseAnalytics: FirebaseAnalytics? = null

	fun init() {
		if (mFirebaseAnalytics == null) {
			mFirebaseAnalytics = FirebaseAnalytics.getInstance(App.instance.applicationContext)
		}
	}

	fun logEvent(eventName: String) {
		if (mFirebaseAnalytics == null) init()
		val bundle = Bundle()
		bundle.putString("app version", BuildConfig.VERSION_NAME)
		try {
			mFirebaseAnalytics?.logEvent(eventName, bundle)
			LogUtils.logCustomMessage("Firebase Event: $eventName")
		} catch (e: Exception) {
			LogUtils.logCustomMessage("Fail log event because: " + e.message)
		}
	}
}