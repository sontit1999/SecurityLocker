package com.ls.entertainment.securitylocker.model

import android.graphics.drawable.Drawable
import com.ls.entertainment.securitylocker.utils.NumberUtil

data class UsageTimeAppModel(
	val appName: String,
	val logo: Drawable,
	val packageName: String,
	val timeUseInForeground: Long
) {
	fun getTimeUsage(): String {
		return NumberUtil.convertTimeToHour(timeUseInForeground)
	}
}