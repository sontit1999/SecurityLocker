package com.ls.entertainment.securitylocker.worker

import android.content.Context
import android.os.Bundle
import androidx.work.*
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.utils.*
import java.util.concurrent.TimeUnit

class NotificationOneHourAfterUnplug(appContext: Context, workerParams: WorkerParameters) :
	Worker(appContext, workerParams) {

	override fun doWork(): Result {
		TrackingHelper.logEvent(AllEvents.WORKER_ONE_HOUR_AFTER_UNPLUG)
		val bundle = Bundle()
		bundle.putString(
			NotificationCenter.MESSAGE,
			applicationContext.getString(R.string.boost_suggest_notify)
		)
		bundle.putString(
			NotificationCenter.TITLE,
			applicationContext.getString(R.string.optimize_now)
		)
		bundle.putString(NotificationCenter.ACTION, NotificationCenter.ACTION_NOTIFICATION_ONE_HOUR_AFTER_UNPLUG)
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