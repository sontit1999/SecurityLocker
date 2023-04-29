package com.ls.entertainment.securitylocker.worker

import android.content.Context
import android.os.Bundle
import androidx.work.ExistingWorkPolicy
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

class NotificationOneHourAfterUnplug(appContext: Context, workerParams: WorkerParameters) :
	Worker(appContext, workerParams) {

	override fun doWork(): Result {
		val bundle = Bundle()
		bundle.putString(
			NotificationCenter.MESSAGE,
			applicationContext.getString(R.string.boost_suggest_notify)
		)
		bundle.putString(
			NotificationCenter.TITLE,
			applicationContext.getString(R.string.optimize_now)
		)
		bundle.putString(NotificationCenter.ACTION, NotificationCenter.ACTION_NOTIFICATION)
		NotificationCenter.push(bundle, isFromFCM = false)
		return Result.success()
	}
	
	companion object {
		const val TAGS = "NotificationOneHourAfterUnplug"

		fun schedule() {
			LogUtils.logCustomMessage("Schedule NotificationOneHourAfterUnplug")
			val delta = RemoteConfig.commonConfig.timeNotifyAfterUnplugInSecond * 1000L
			val work: OneTimeWorkRequest =
				OneTimeWorkRequest.Builder(NotificationOneHourAfterUnplug::class.java)
					.setInitialDelay(delta, TimeUnit.MILLISECONDS).build()
			WorkManager.getInstance(App.instance)
				.enqueueUniqueWork(TAGS, ExistingWorkPolicy.REPLACE, work)
		}

		fun cancel() {
			LogUtils.logCustomMessage("Cancel NotificationOneHourAfterUnplug")
			WorkManager.getInstance(App.instance).cancelUniqueWork(TAGS)
		}
	}
}