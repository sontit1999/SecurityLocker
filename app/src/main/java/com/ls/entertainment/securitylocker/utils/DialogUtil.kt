package com.ls.entertainment.securitylocker.utils


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.entertainment.basemvvmproject.utils.visible
import com.ls.entertainment.securitylocker.R


object DialogUtil {

	fun showConfirmationDialog(
		context: Context?,
		textTitle: Any? = null,
		textMessage: Any,
		textOk: Any = context?.getString(R.string.msg_ok) ?: "",
		textCancel: Any? = null,
		okListener: (() -> Unit)? = null,
		cancelListener: (() -> Unit)? = null,
		cancelable: Boolean = false
	) {
		context?.run {
			val dialog = Dialog(context)
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
			dialog.window?.setBackgroundDrawableResource(R.color.transparent)
			dialog.setContentView(R.layout.dialog_confirmation)
			dialog.setCancelable(cancelable)

			val lblTitle = dialog.findViewById<TextView>(R.id.lbl_title)
			val lblMessage = dialog.findViewById<TextView>(R.id.lbl_message)
			val btnOk = dialog.findViewById<TextView>(R.id.btn_ok)
			val btnCancel = dialog.findViewById<TextView>(R.id.btn_cancel)

			textTitle?.let {
				lblTitle.visible()
				lblTitle.text = when (it) {
					is String       -> it
					is CharSequence -> it
					is Int          -> context.getString(it)
					else            -> ""
				}
			}

			lblMessage.text = when (textMessage) {
				is String       -> textMessage
				is CharSequence -> textMessage
				is Int          -> context.getString(textMessage)
				else            -> ""
			}

			btnOk.text = when (textOk) {
				is String       -> textOk
				is CharSequence -> textOk
				is Int          -> context.getString(textOk)
				else            -> ""
			}
			btnOk.setOnSafeClickListener {
				if (dialog.isShowing) {
					dialog.dismiss()
					okListener?.invoke()
				}
			}

			val strCancel = when (textCancel) {
				is String       -> textCancel
				is CharSequence -> textCancel
				is Int          -> context.getString(textCancel)
				else            -> ""
			}
			if (strCancel.isEmpty() || strCancel.isBlank()) {
				btnCancel.visibility = View.GONE
			} else {
				btnCancel.text = strCancel
				btnCancel.setOnSafeClickListener {
					if (dialog.isShowing) {
						dialog.dismiss()
						cancelListener?.invoke()
					}
				}
			}

			if (!dialog.isShowing) {
				dialog.show()
			}
		}
	}

	fun showConfirmationNetworkDialog(
		context: Context?,
		okListener: (() -> Unit)? = null,
	) {
		context?.run {
			val dialog = Dialog(context)
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
			dialog.window?.setBackgroundDrawableResource(R.color.transparent)
			dialog.setContentView(R.layout.dialog_internet)
			dialog.setCancelable(false)

			val btnOk = dialog.findViewById<TextView>(R.id.btn_ok)


			btnOk.setOnSafeClickListener {
				if (dialog.isShowing) {
					TrackingHelper.logEvent(AllEvents.ACTION_CLICK_OPEN_WIFI)
					dialog.dismiss()
					context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
				}
			}

			if (!dialog.isShowing) {
				TrackingHelper.logEvent(AllEvents.VIEW_NO_NETWORK)
				dialog.show()
			}
		}
	}

	fun showSetWallpaperDialog(
		context: Context?,
		lockAppListener: (() -> Unit)? = null,
		lockHomeListener: (() -> Unit)? = null,
		lockListener: (() -> Unit)? = null,
	) {
		context?.run {
			val dialog = Dialog(context)
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
			dialog.window?.setBackgroundDrawableResource(R.color.transparent)
			dialog.setContentView(R.layout.dialog_confirm_set_background)

			val lockApp = dialog.findViewById<TextView>(R.id.dialogConfirmLockApp)
			val lockHome = dialog.findViewById<TextView>(R.id.dialogConfirmHome)
			val lock = dialog.findViewById<TextView>(R.id.dialogConfirmLock)


			lockApp.setOnSafeClickListener {
				if (dialog.isShowing) {
					dialog.dismiss()
					lockAppListener?.invoke()
				}
			}

			lockHome.setOnSafeClickListener {
				if (dialog.isShowing) {
					dialog.dismiss()
					lockHomeListener?.invoke()
				}
			}

			lock.setOnSafeClickListener {
				if (dialog.isShowing) {
					dialog.dismiss()
					lockListener?.invoke()
				}
			}

			if (!dialog.isShowing) {
				dialog.show()
			}
		}
	}

	fun showCongratulationDialog(
		context: Context?,
		OkeListener: (() -> Unit)? = null
	) {
		context?.run {
			TrackingHelper.logEvent(AllEvents.VIEW_CONGRATULATION)
			val dialog = Dialog(context)
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
			dialog.window?.setBackgroundDrawableResource(R.color.transparent)
			dialog.setContentView(R.layout.dialog_congratulation)
			dialog.setCancelable(false)
			val btnOke = dialog.findViewById<AppCompatButton>(R.id.btnOke)

			btnOke.setOnSafeClickListener {
				if (dialog.isShowing) {
					dialog.dismiss()
					OkeListener?.invoke()
				}
			}
			if (!dialog.isShowing) {
				dialog.show()
			}
		}
	}

	fun showConfirmationWatchAdDialog(
		context: Context?,
		okListener: (() -> Unit)? = null,
		cancelListener: (() -> Unit)? = null,
	) {
		context?.run {
			val dialog = Dialog(context)
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
			dialog.window?.setBackgroundDrawableResource(R.color.transparent)
			dialog.setContentView(R.layout.dialog_confirm_watch_ads)
			dialog.setCancelable(false)

			val btnOk = dialog.findViewById<TextView>(R.id.btn_ok)
			val btnCancel = dialog.findViewById<TextView>(R.id.btn_cancel)

			btnOk.setOnSafeClickListener {
				TrackingHelper.logEvent(AllEvents.ACTION_ACCEPT_DOWNLOAD)
				if (dialog.isShowing) {
					okListener?.invoke()
					dialog.dismiss()
				}
			}

			btnCancel.setOnSafeClickListener {
				TrackingHelper.logEvent(AllEvents.ACTION_DENY_DOWNLOAD)
				if (dialog.isShowing) {
					cancelListener?.invoke()
					dialog.dismiss()
				}
			}

			if (!dialog.isShowing) {
				dialog.show()
			}
		}
	}
}