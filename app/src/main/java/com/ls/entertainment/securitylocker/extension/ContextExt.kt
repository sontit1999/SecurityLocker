package com.ls.entertainment.securitylocker.extension

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ls.entertainment.securitylocker.utils.PermissionUtil
import com.ls.entertainment.securitylocker.utils.checkUsageStatsPermission

fun Context.canDrawOverlay(): Boolean {
    return PermissionUtil.isApi23orHigher() && Settings.canDrawOverlays(this)
}

fun Context.requestDrawOverlayPermission(requestFrom: Any, requestCode: Int) {
    if (PermissionUtil.isApi23orHigher()) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
		when (requestFrom) {
			is AppCompatActivity -> {
				requestFrom.startActivityForResult(intent, requestCode)
			}
		
			is Fragment -> {
				requestFrom.startActivityForResult(intent, requestCode)
			}
		}
	}
}

fun Context.isAllowAllPermission(): Boolean {
	return this.canDrawOverlay() && this.checkUsageStatsPermission()
}