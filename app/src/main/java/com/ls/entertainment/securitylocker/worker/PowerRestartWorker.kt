package com.ls.entertainment.securitylocker.worker

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.service.LockService
import java.util.concurrent.TimeUnit

class PowerRestartWorker(private val appContext: Context, workerParams: WorkerParameters) :
	Worker(appContext, workerParams) {

	@RequiresApi(Build.VERSION_CODES.O)
	override fun doWork(): Result {
		appContext.startForegroundService(Intent(appContext, LockService::class.java))
		// Indicate whether the work finished successfully with the Result
		return Result.success()
	}

	companion object {

		private const val TAG = "PowerRestartService"

		fun schedule() {
			val delayTimeSecond = 2
			val work = OneTimeWorkRequest.Builder(PowerRestartWorker::class.java)
				.setInitialDelay(delayTimeSecond.toLong(), TimeUnit.SECONDS).addTag(TAG).build()
			WorkManager.getInstance(App.instance).beginWith(work).enqueue()
		}

	}
}


