package com.ls.entertainment.securitylocker.worker


import android.content.Context
import android.os.Bundle
import androidx.work.*
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.utils.*
import java.util.*
import java.util.concurrent.TimeUnit

class NotificationOfflineWorker(
	appContext: Context, workerParams: WorkerParameters
) : Worker(appContext, workerParams) {


	private val listNotification = arrayOf(
		Pair(R.string.msg_dayOneNotificationTitle01, R.string.dayOneNotificationMessage01),
		Pair(R.string.dayFourNotificationTitle03, R.string.dayFourNotificationMessage03),
		Pair(R.string.daySevenNotificationTitle02, R.string.daySevenNotificationMessage02),
		Pair(R.string.dayTwoNotificationTitle01, R.string.dayTwoNotificationMessage01),
		Pair(R.string.weeklyNotificationTitle04, R.string.weeklyNotificationMessage04),
	)

	override fun doWork(): Result {
		TrackingHelper.logEvent(AllEvents.WORKER_OFFLINE)
		pushNotification()
		reschedule()
		return Result.success()
	}

	private fun reschedule() {
		try {
			schedule()
		} catch (e: Exception) {
			LogUtils.logCustomMessage(e.message.toString())
		}
	}

	private fun pushNotification() {
		try {
			val index = SharePreferenceUtils.getInstance().indexNotification
			val data = listNotification[index]
			SharePreferenceUtils.getInstance().indexNotification =
				if (index < listNotification.size - 1) index + 1 else 0
			val bundle = Bundle()
			bundle.putString(NotificationCenter.TITLE, applicationContext.getString(data.first))
			bundle.putString(NotificationCenter.MESSAGE, applicationContext.getString(data.second))
			bundle.putString(NotificationCenter.ACTION, NotificationCenter.ACTION_NOTIFICATION_REMIND_OPEN)
			NotificationCenter.push(bundle, isFromFCM = false)
		} catch (e: Exception) {
			LogUtils.logCustomMessage("Do work notification worker exception :" + e.message)
		}
	}

	companion object {
		const val TAGS = "NotificationOfflineWorker"
		fun schedule() {
			LogUtils.logCustomMessage("Schedule notification remind open app offline")
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
				OneTimeWorkRequest.Builder(NotificationOfflineWorker::class.java)
					.setInitialDelay(delta, TimeUnit.MILLISECONDS).setConstraints(constraints)
					.build()
			WorkManager.getInstance(App.instance)
				.enqueueUniqueWork(TAGS, ExistingWorkPolicy.REPLACE, work)
		}

		fun cancel() {
			LogUtils.logCustomMessage("Cancel notification remind open app offline")
			WorkManager.getInstance(App.instance).cancelUniqueWork(TAGS)
		}
	}

}