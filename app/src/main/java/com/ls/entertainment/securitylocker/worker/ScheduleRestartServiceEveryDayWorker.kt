package com.ls.entertainment.securitylocker.worker


import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.*
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.service.LockService
import com.ls.entertainment.securitylocker.utils.LogUtils
import java.util.concurrent.TimeUnit

class ScheduleRestartServiceEveryDayWorker(
	private val appContext: Context, workerParams: WorkerParameters
) : Worker(appContext, workerParams) {


	override fun doWork(): Result {
		// check service restart if need
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			appContext.startForegroundService(Intent(appContext, LockService::class.java))
		} else {
			appContext.startService(Intent(appContext, LockService::class.java))
		}
		reschedule()
		return Result.success()
	}

	private fun reschedule() {
		try {
			schedule()
		} catch (e: Exception) {
			LogUtils.logCustomMessage(TAGS, e.message.toString())
		}
	}


	companion object {
		const val TAGS = "ScheduleRestartServiceEveryDayWorker"
		fun schedule() {
			val constraints =
				Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
			val delta = 24 * 60 * 60 * 1000L
			val work: OneTimeWorkRequest =
				OneTimeWorkRequest.Builder(ScheduleRestartServiceEveryDayWorker::class.java)
					.setInitialDelay(delta, TimeUnit.MILLISECONDS).setConstraints(constraints)
					.build()
			WorkManager.getInstance(App.instance)
				.enqueueUniqueWork(TAGS, ExistingWorkPolicy.REPLACE, work)
		}

		fun cancel() {
			LogUtils.logCustomMessage("Cancel worker restart service")
			WorkManager.getInstance(App.instance).cancelUniqueWork(TAGS)
		}
	}

}