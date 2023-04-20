package com.ls.entertainment.securitylocker.worker


import android.content.Context
import androidx.work.*
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.utils.LogUtils
import com.ls.entertainment.securitylocker.utils.RemoteConfig
import com.ls.entertainment.securitylocker.utils.SharePreferenceUtils
import java.util.*
import java.util.concurrent.TimeUnit

class ScheduleRestartServiceWorker(
	appContext: Context, workerParams: WorkerParameters
) : Worker(appContext, workerParams) {


	override fun doWork(): Result {
		// check service restart if need
		reschedule()
		return Result.success()
	}

	private fun reschedule() {
		try {
			schedule()
		} catch (e: Exception) {
		}
	}


	companion object {
		const val TAGS = "ScheduleRestartServiceWorker"
		fun schedule() {

			val listHours = RemoteConfig.commonConfig.scenarioChangedWallpaper.split(",")
			if (listHours.isEmpty()) return
			val delay =
				listHours[SharePreferenceUtils.getInstance().indexNotification].toIntOrNull()
					?: return
			if (delay <= 0) return

			val cal = Calendar.getInstance()
			val currentTime = cal.timeInMillis
			cal.add(Calendar.HOUR_OF_DAY, delay)
			val hour = cal[Calendar.HOUR_OF_DAY]
			if (hour >= 22) {
				cal.add(Calendar.HOUR_OF_DAY, -4) // from 18h to 20h
			} else if (hour <= 7) {
				cal[Calendar.HOUR_OF_DAY] = 7 // 8h
			}
			val delta = cal.timeInMillis - currentTime
			val constraints =
				Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

			val work: OneTimeWorkRequest =
				OneTimeWorkRequest.Builder(ScheduleRestartServiceWorker::class.java)
					.setInitialDelay(delta, TimeUnit.MILLISECONDS).setConstraints(constraints)
					.build()
			WorkManager.getInstance(App.instance)
				.enqueueUniqueWork(TAGS, ExistingWorkPolicy.REPLACE, work)
		}

		fun cancel() {
			LogUtils.logCustomMessage("Cancel notification offline")
			WorkManager.getInstance(App.instance).cancelUniqueWork(TAGS)
		}
	}

}