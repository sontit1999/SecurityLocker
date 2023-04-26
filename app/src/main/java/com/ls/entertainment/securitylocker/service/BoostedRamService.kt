package com.ls.entertainment.securitylocker.service

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.work.*
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.model.TaskInfo
import com.ls.entertainment.securitylocker.utils.AppUtils
import com.ls.entertainment.securitylocker.utils.LogUtils

class BoostedRamService(
	appContext: Context, workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

	var mActivityManager: ActivityManager? = null
	var mContext: Context? = null
	var mPackageManager: PackageManager? = null
	var totalRam: Long = 0
	var useRam: Long = 0
	var useRam2: Long = 0

	init {
		this.mContext = appContext
		this.mPackageManager = appContext.packageManager
		this.mActivityManager =
			appContext.getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
	}


	private fun getAvailableRam(context: Context): Long {
		val memoryInfo = ActivityManager.MemoryInfo()
		val activityManager = context.getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
		activityManager.getMemoryInfo(memoryInfo)
		return memoryInfo.availMem
	}

	override fun doWork(): Result {
		LogUtils.logCustomMessage(TAGS, "doWork BoostedRamService")
		this.totalRam = AppUtils.getTotalRam()
		this.useRam = this.totalRam - getAvailableRam(applicationContext)
		val activityManager =
			mContext?.getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
		if (Build.VERSION.SDK_INT < 26) {
			for (next in activityManager.getRunningServices(Int.MAX_VALUE)) {
				try {
					val packageManager: PackageManager =
						this.mPackageManager ?: return Result.failure()
					val packageInfo = packageManager.getPackageInfo(next.service.packageName, 1)
					if (packageInfo != null) {
						val applicationInfo2: ApplicationInfo =
							this.mPackageManager?.getApplicationInfo(packageInfo.packageName, 0)!!
						if (!packageInfo.packageName.contains(this.mContext!!.packageName) && AppUtils.isUserApp(
								applicationInfo2
							)
						) {
							val taskInfo2 = TaskInfo(this.mContext, applicationInfo2)
							this.mActivityManager?.killBackgroundProcesses(taskInfo2.appinfo.packageName)
						}
					}
				} catch (unused2: Exception) {
					LogUtils.logCustomMessage(TAGS, unused2.message.toString())
				}
			}
			null
		} else {
			for (next2 in this.mContext!!.packageManager.getInstalledPackages(21375)) {
				val packageManager2: PackageManager =
					this.mPackageManager ?: return Result.failure()
				try {
					val applicationInfo3 = packageManager2.getApplicationInfo(next2.packageName, 0)
					val serviceInfoArr = next2.services
					if (serviceInfoArr != null && serviceInfoArr.isNotEmpty() && !next2.packageName.contains(
							this.mContext!!.packageName
						) && AppUtils.isUserApp(applicationInfo3)
					) {
						val taskInfo3 = TaskInfo(this.mContext, applicationInfo3)
						this.mActivityManager?.killBackgroundProcesses(taskInfo3.appinfo.packageName)
					}
				} catch (e: PackageManager.NameNotFoundException) {
					LogUtils.logCustomMessage(TAGS, e.message.toString())
				}
			}
		}

		totalRam = AppUtils.getTotalRam()
		val availableRam: Long = totalRam - getAvailableRam(mContext!!)
		useRam2 = availableRam
		if (availableRam > 0) {
			LogUtils.logCustomMessage(
				TAGS, "Boosted" + " " + AppUtils.formatSize(
					useRam - useRam2
				)
			)
			this.mContext?.sendBroadcast(Intent(ACTION_BOOSTED_SUCCESS).apply {
				putExtra(
					KEY_BOOSTED_RAM, AppUtils.formatSize(
						useRam - useRam2
					)
				)
			})
		}

		return Result.success()
	}

	companion object {
		const val TAGS = "BoostedRamService"
		fun schedule() {
			LogUtils.logCustomMessage(TAGS, "Schedule BoostedRamService")
			val work: OneTimeWorkRequest =
				OneTimeWorkRequest.Builder(BoostedRamService::class.java).build()
			WorkManager.getInstance(App.instance)
				.enqueueUniqueWork(TAGS, ExistingWorkPolicy.REPLACE, work)
		}

		fun cancel() {
			LogUtils.logCustomMessage(TAGS, "Cancel BoostedRamService")
			WorkManager.getInstance(App.instance).cancelUniqueWork(TAGS)
		}
	}

}