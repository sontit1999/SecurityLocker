package com.ls.entertainment.securitylocker.worker

import android.content.Context
import android.os.Bundle
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.utils.LogUtils
import com.ls.entertainment.securitylocker.utils.NotificationCenter
import com.ls.entertainment.securitylocker.utils.RemoteConfig
import java.util.concurrent.TimeUnit

class NotificationAfter10mPresentWorker(
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
		const val TAGS = "NotificationAfter10mPresentWorker"
		fun schedule() {
			LogUtils.logCustomMessage("Schedule NotificationAfter10mPresentWorker")
			val constraints =
				Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
			val delta = RemoteConfig.commonConfig.timeNotify10mPresent * 60 * 1000L
			val work: OneTimeWorkRequest =
				OneTimeWorkRequest.Builder(NotificationAfter10mPresentWorker::class.java)
					.setInitialDelay(delta, TimeUnit.MILLISECONDS).setConstraints(constraints)
					.build()
			WorkManager.getInstance(App.instance)
				.enqueueUniqueWork(TAGS, ExistingWorkPolicy.REPLACE, work)
		}

		fun cancel() {
			LogUtils.logCustomMessage("Cancel NotificationAfter10mPresentWorker")
			WorkManager.getInstance(App.instance).cancelUniqueWork(TAGS)
		}
	}

}