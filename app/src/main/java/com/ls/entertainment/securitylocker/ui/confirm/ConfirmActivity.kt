package com.ls.entertainment.securitylocker.ui.confirm

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import com.entertainment.basemvvmproject.base.BaseActivity
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.databinding.ActivityConfirmBinding
import com.ls.entertainment.securitylocker.ui.splash.SplashActivity
import com.ls.entertainment.securitylocker.utils.setOnSafeClickListener

class ConfirmActivity : BaseActivity<ActivityConfirmBinding, ConfirmViewModel>() {

	private val viewModel: ConfirmViewModel by viewModels()

	override fun getVM() = viewModel

	override val layoutId = R.layout.activity_confirm

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		showWhenLockedAndTurnScreenOn()
		binding.btnNo.setOnSafeClickListener { finish() }
		binding.btnYes.setOnSafeClickListener {
			val intent = Intent(this, SplashActivity::class.java)
			intent.putExtra(SplashActivity.KEY_TYPE_OPTIMIZE, SplashActivity.TYPE_FROM_FAST_CHARGER)
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
			startActivity(intent)
		}
	}

	override fun onBackPressed() {

	}

	private fun showWhenLockedAndTurnScreenOn() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
			setShowWhenLocked(true)
			setTurnScreenOn(true)
		} else {
			window.addFlags(
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
			)
		}
	}
}