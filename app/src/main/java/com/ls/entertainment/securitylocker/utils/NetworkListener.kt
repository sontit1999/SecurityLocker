package com.ls.entertainment.securitylocker.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import androidx.lifecycle.LiveData
import com.ls.entertainment.securitylocker.App
import java.lang.ref.WeakReference

object NetworkListener : LiveData<Boolean>() {

	private var weakReference: WeakReference<Context>? = WeakReference(App.instance)

	fun isNetWorkConnected() = this.value != null && this.value == true

	private val networkCallback = object : ConnectivityManager.NetworkCallback() {
		override fun onAvailable(network: Network) {
			postValue(true)
		}

		override fun onLost(network: Network) {
			postValue(false)
		}
	}

	override fun onActive() {
		super.onActive()
		weakReference?.get()?.registerReceiver(
			networkReceiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
		)
		request()
	}

	override fun onInactive() {
		super.onInactive()
		AppConfig.connectivityManager.unregisterNetworkCallback(networkCallback)
		weakReference?.get()?.unregisterReceiver(networkReceiver)
		weakReference?.clear()
		weakReference = null
	}

	private fun request() {
		val builder =
			NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
				.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
				.addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
		AppConfig.connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
	}

	private val networkReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			updateConnection()
		}
	}

	@Suppress("DEPRECATION")
	private fun updateConnection() {
		val activeNetwork: NetworkInfo? = AppConfig.connectivityManager.activeNetworkInfo
		postValue(activeNetwork?.isConnectedOrConnecting == true)
	}
}