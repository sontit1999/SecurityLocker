package com.ls.entertainment.securitylocker.utils


import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.BuildConfig
import com.ls.entertainment.securitylocker.model.UsageTimeAppModel
import java.util.*


object PackageUtil {

	fun getAppID(): String {
		return BuildConfig.APPLICATION_ID
	}

	suspend fun getTimeUserAppInstalled24Hour(): MutableList<UsageTimeAppModel> {
		val listApp = mutableListOf<UsageTimeAppModel>()
		val mUsageStatsManager =
			App.instance.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
		val calendar = Calendar.getInstance()
		val endTime = calendar.timeInMillis
		calendar.add(Calendar.DATE, -1)
		val startTime = calendar.timeInMillis
		val list =
			mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
		val listNonSystemApp = getInstallAppPackage()
		list.forEach {
			if (listNonSystemApp.contains(it.packageName)) {
				val appInfor = App.instance.packageManager.getApplicationInfo(it.packageName, 0)
				val appName = appInfor.loadLabel(App.instance.packageManager)
				val drawableRes = appInfor.loadIcon(App.instance.packageManager)
				listApp.add(
					UsageTimeAppModel(
						appName.toString(),
						drawableRes,
						it.packageName,
						it.totalTimeInForeground
					)
				)
			}
		}
		listApp.sortByDescending { it.timeUseInForeground }
		return listApp
	}
	
	@RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
	fun getTimeUserAppInstalledLast10Day(): MutableList<UsageTimeAppModel> {
		val listApp = mutableListOf<UsageTimeAppModel>()
		val mUsageStatsManager =
			App.instance.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
		val calendar = Calendar.getInstance()
		val endTime = calendar.timeInMillis
		calendar.add(Calendar.DATE, -10)
		val startTime = calendar.timeInMillis
		val list =
			mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime)
		val listNonSystemApp = getInstallAppPackage()
		list.forEach {
			if (listNonSystemApp.contains(it.packageName)) {
				val appInfor = App.instance.packageManager.getApplicationInfo(it.packageName, 0)
				val appName = appInfor.loadLabel(App.instance.packageManager)
				val drawableRes = appInfor.loadIcon(App.instance.packageManager)
				listApp.add(
					UsageTimeAppModel(
						appName.toString(),
						drawableRes,
						it.packageName,
						it.totalTimeInForeground
					)
				)
			}
		}
		listApp.sortByDescending { it.timeUseInForeground }
		return listApp
	}


	private fun getInstallAppPackage(): MutableList<String> {
		val list =
			App.instance.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
		val listNonSystemApp = mutableListOf<String>()
		list.forEach {
			if (!isSystemPackage(it)) {
				listNonSystemApp.add(it.packageName)
			}
		}
		return listNonSystemApp
	}

	private fun isSystemPackage(applicationInfo: ApplicationInfo): Boolean {
		return applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
	}

	fun uninstallPackage(packageName: String) {
		val packageURI = Uri.parse("package:$packageName")
		val intent = Intent(Intent.ACTION_DELETE, packageURI)
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		App.instance.startActivity(intent)
	}
}