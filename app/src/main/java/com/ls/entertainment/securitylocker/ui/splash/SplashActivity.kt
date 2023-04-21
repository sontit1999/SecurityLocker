package com.ls.entertainment.securitylocker.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.viewModelScope
import com.entertainment.basemvvmproject.base.BaseActivity
import com.ls.entertainment.securitylocker.MainActivity
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.databinding.ActivitySplashBinding
import com.ls.entertainment.securitylocker.service.LockService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {

	private val viewModel: SplashViewModel by viewModels()

	override val layoutId = R.layout.activity_splash

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		LockService.startLockService(this)
		viewModel.viewModelScope.launch {
			delay(2000)
			startActivity(Intent(this@SplashActivity, MainActivity::class.java))
			finish()
		}
	}

	override fun getVM() = viewModel
}