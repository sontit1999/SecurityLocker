package com.ls.entertainment.securitylocker.utils

import android.bluetooth.BluetoothAdapter
import android.content.ContentResolver
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import com.ls.entertainment.securitylocker.R

inline val Context.ctx: Context
	get() = this

inline val Context.manufacturer: String
	get() = Build.MANUFACTURER

inline val Context.isXiaomiDevice: Boolean
	get() = manufacturer.equals("Xiaomi", true)

inline val Context.isSamsungDevice: Boolean
	get() = manufacturer.equals("Samsung", true)

inline val Context.isOppoDevice: Boolean
	get() = manufacturer.equals("Oppo", true)

inline val Context.isVivoDevice: Boolean
	get() = manufacturer.equals("Vivo", true)

inline val Context.isAutoRotationEnabled: Boolean
	get() = Settings.System.getInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0) == 1

inline val Context.isAutoSyncEnabled: Boolean
	get() = ContentResolver.getMasterSyncAutomatically()

inline val Context.isWifiEnabled: Boolean
	get() {
		val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
		return wifiManager?.isWifiEnabled ?: false
	}

inline val Context.isBluetoothEnabled: Boolean
	get() {
		val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
		return bluetoothAdapter?.isEnabled ?: false
	}

/*fun Context.shouldShowAds(): Boolean {
    return adsConfigModel.isAdsEnabled && !appSettingsModel.didRemoveAds
}

fun Context.shouldShowAdsRemovalFeature(): Boolean {
    return shouldShowAds() && adsConfigModel.isAdsRemovalEnabled
}

fun Context.networkIsConnected(): Boolean {
    try {
        val conMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return conMgr?.let {
            return if (PermissionUtil.isApi29orHigher()) {
                val capabilities = it.getNetworkCapabilities(it.activeNetwork)
                capabilities?.run {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                } ?: false
            } else {
                it.activeNetworkInfo?.isConnected ?: false
            }
        } ?: false
    } catch (e: Exception) {
        logE("$e")
    }

    return false
}

fun Context.canWriteSettings(): Boolean {
    return (PermissionUtil.isApi23orHigher() && Settings.System.canWrite(this))
            || !PermissionUtil.isApi23orHigher()
}

fun Context.requestWriteSettingsPermission(requestFrom: Any, requestCode: Int) {
    if (PermissionUtil.isApi23orHigher()) {
        Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
            data = Uri.parse("package:${ctx.packageName}")
        }.run {
            when (requestFrom) {
                is AppCompatActivity -> {
                    requestFrom.startActivityForResult(this, requestCode)
                }
                is Fragment -> {
                    requestFrom.startActivityForResult(this, requestCode)
                }
            }
        }
    }
}

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

fun Context.getInstalledApps(): List<ApplicationInfo> {
    return try {
        packageManager.getInstalledApplications(0).filter {
            (it.flags and ApplicationInfo.FLAG_SYSTEM) != 1 && it.packageName != packageName
        }
    } catch (e: java.lang.Exception) {
        listOf()
    }
}

fun Context.toggleBluetooth(enable: Boolean): Boolean {
    val targetSdkVersion = applicationInfo.targetSdkVersion
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && targetSdkVersion >= Build.VERSION_CODES.S) {
        return if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED) {
            return false
        } else {
            if (enable) {
                bluetoothAdapter?.enable()
            } else {
                bluetoothAdapter?.disable()
            } ?: false
        }
    } else {
        return if (enable) {
            bluetoothAdapter?.enable()
        } else {
            bluetoothAdapter?.disable()
        } ?: false
    }
}

fun Context.requestAppUsageAccessPermission(requestFrom: Any, requestCode: Int) {
    if (PermissionUtil.isApi23orHigher()) {
        val intent = Intent(
            Settings.ACTION_USAGE_ACCESS_SETTINGS,
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

fun Context.getAppUsageStatsList(): List<UsageStats> {
    if (PermissionUtil.isApi23orHigher()) {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
        usageStatsManager?.let {
            val cal = Calendar.getInstance()
            val endTime = cal.timeInMillis
            cal.add(Calendar.DAY_OF_MONTH, -1)
            val startTime = cal.timeInMillis
            return it.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
        }
    }

    return emptyList()
}

fun Context.toggleAutoRotation(value: Int): Boolean {
    return Settings.System.putInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION, value)
}

fun Context.toggleAutoSync(enable: Boolean) {
    ContentResolver.setMasterSyncAutomatically(enable)
}

fun Context.toggleWifi(enable: Boolean) {
    if (!PermissionUtil.isApi29orHigher()) {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        wifiManager?.isWifiEnabled = enable
    }
}

fun Context.durationBattery() {
    if (!PermissionUtil.isApi29orHigher()) {
        val powerManager =
            applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager?
        powerManager?.batteryDischargePrediction
    }
}


fun Context.checkPermissions(permissions: Array<String>): Array<String> {
    val array = mutableListOf<String>()
    for (p in permissions) {
        if (PackageManager.PERMISSION_GRANTED != packageManager.checkPermission(p, packageName))
            array.add(p)
    }
    return array.toTypedArray()
}

fun Context.openOtherPermissionsPageOnXiaomiDevice() {
    try {
        Intent("miui.intent.action.APP_PERM_EDITOR").apply {
            setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
            )
            putExtra("extra_pkgname", packageName)
        }.run {
            startActivity(this)
        }
    } catch (e: Exception) {
    }
}


fun Context.showAdsRemovingDialog(okListener: () -> Unit) {
    DialogUtil.showConfirmationDialog(
        this, R.string.title_alert_buy, R.string.message_alert_buy,
        R.string.ok, R.string.cancel, cancelable = false,
        okListener = {
            if (networkIsConnected()) {
                okListener()
            } else {
                toast(R.string.alert_buy_error)
            }
        }
    )
}

fun Context.showDrawOverlayPermissionDescDialog(
    onOkListener: () -> Unit,
    onCancelListener: () -> Unit
) {
    val message =
        getString(R.string.desc_permission_draw_overlay) + "\n" + "\n" + getString(R.string.guide_access_permission_v5)
    DialogUtil.showConfirmationDialog(
        ctx, R.string.grant_permission, message,
        R.string.grant_permission_now,"",
        okListener = {
            onOkListener.invoke()
        }, cancelListener = {
            onCancelListener.invoke()
        }
    )
}

fun Context.showRequireUpdateDialog(
    onOkListener: () -> Unit,
    onCancelListener: () -> Unit,
    canCancel : Boolean = false
) {
    val message =
        getString(R.string.desc_require_update_app) + "\n" + "\n" + getString(R.string.guide_update_app)
    DialogUtil.showConfirmationDialog(
        ctx, R.string.title_update_app, message,
        R.string.ok,if(canCancel) R.string.cancel else "",
        okListener = {
            onOkListener.invoke()
        }, cancelListener = {
            onCancelListener.invoke()
        },cancelable = canCancel
    )
}*/

fun Context.showAccessDataUsagePermissionDialog(
	onOkListener: () -> Unit, onCancelListener: () -> Unit
) {
	val message =
		getString(R.string.desc_permission_access_data_usage) + "\n" + "\n" + getString(R.string.guide_access_permission_v5)
	DialogUtil.showConfirmationDialog(ctx,
		R.string.grant_permission,
		message,
		R.string.grant_permission_now,
		getString(R.string.cancel),
		okListener = {
			onOkListener.invoke()
		},
		cancelListener = {
			onCancelListener.invoke()
		})
}

