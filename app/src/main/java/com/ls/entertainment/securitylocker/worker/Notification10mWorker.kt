package com.ls.entertainment.securitylocker.worker

import android.content.Context
import android.os.Bundle
import androidx.work.*
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.utils.LogUtils
import com.ls.entertainment.securitylocker.utils.NotificationCenter
import com.ls.entertainment.securitylocker.utils.RemoteConfig
import java.util.concurrent.TimeUnit

class Notification10mWorker(
	appContext: Context, workerParams: WorkerParameters
) : Worker(appContext, workerParams) {


	override fun doWork(): Result {
		pushNotification()
		return Result.success()
	}

	private fun pushNotification() {
		try {
			val bundle = Bundle()
			bundle.putString(
				NotificationCenter.TITLE, App.instance.getString(R.string.NotificationTitle10m)
			)
			bundle.putString(
				NotificationCenter.MESSAGE, App.instance.getString(R.string.NotificationMessage10m)
			)
			bundle.putString(NotificationCenter.ACTION, NotificationCenter.ACTION_NOTIFICATION)
			NotificationCenter.push(bundle, isFromFCM = false)
		} catch (e: Exception) {
			LogUtils.logCustomMessage("Do work notification worker exception :" + e.message)
		}
	}

	companion object {
		const val TAGS = "NotificationWorker10m"
		fun schedule() {
			LogUtils.logCustomMessage("Schedule notification 10m")
			val constraints =
				Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
			val delta = RemoteConfig.commonConfig.timeNotify10m * 60 * 1000L
			val work: OneTimeWorkRequest =
				OneTimeWorkRequest.Builder(Notification10mWorker::class.java)
					.setInitialDelay(delta, TimeUnit.MILLISECONDS).setConstraints(constraints)
					.build()
			WorkManager.getInstance(App.instance)
				.enqueueUniqueWork(TAGS, ExistingWorkPolicy.REPLACE, work)
		}

		fun cancel() {
			LogUtils.logCustomMessage("Cancel notification 10m")
			WorkManager.getInstance(App.instance).cancelUniqueWork(TAGS)
		}
	}

}