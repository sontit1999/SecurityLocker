package com.ls.entertainment.securitylocker.worker

import android.content.Context
import androidx.work.*
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.utils.Constant
import com.ls.entertainment.securitylocker.utils.LogUtils
import com.ls.entertainment.securitylocker.utils.NotificationCenter
import java.util.concurrent.TimeUnit

class NotificationOneHourAfterUnplug(appContext: Context, workerParams: WorkerParameters) :
	Worker(appContext, workerParams) {

	override fun doWork(): Result {
		val actionNotify = getActionNotifyFromIndex(-1)
		val layoutNotifyId = getLayoutNotifyFromIndex(-1)
		val requestCode = getRequestCodeNotifyFromIndex(-1)
		NotificationCenter.showCustomNotify(actionNotify, layoutNotifyId, requestCode)
		return Result.success()
	}

	private fun getTaskYetOptimize(): Int {
		return -1
	}

	private fun getActionNotifyFromIndex(index: Int): String {
		return Constant.ACTION_OPTIMIZE_BATTERY
	}

	private fun getLayoutNotifyFromIndex(index: Int): Int {
		return R.layout.layout_custom_notification_battery
	}


	private fun getRequestCodeNotifyFromIndex(index: Int): Int {
		return Constant.RC_OPTIMIZE_BATTERY
	}

	companion object {
		const val TAGS = "NotificationOneHourAfterUnplug"

		fun schedule() {
			LogUtils.logCustomMessage("Schedule NotificationOneHourAfterUnplug")
			val delta = 60 * 60 * 1000L
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