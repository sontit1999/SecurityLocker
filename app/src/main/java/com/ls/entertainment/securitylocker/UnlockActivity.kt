package com.ls.entertainment.securitylocker

import android.os.Bundle
import android.view.KeyEvent
import com.example.demoandroidrikkei.base.ui.BaseActivityNotRequireViewModel
import com.ls.entertainment.securitylocker.databinding.ActivityLockBinding

class UnlockActivity : BaseActivityNotRequireViewModel<ActivityLockBinding>() {

	override val layoutId = R.layout.activity_lock

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		App.isShowLock = true
		binding.btnStart.setOnClickListener {
			showToast("Unlock")
		}

	}

	override fun onDestroy() {
		super.onDestroy()
		App.isShowLock = false
	}

	override fun onBackPressed() {

	}

	override fun onKeyDown(key_code: Int, key_event: KeyEvent?): Boolean {
		if (key_code == KeyEvent.KEYCODE_BACK) {
			super.onKeyDown(key_code, key_event)
			return true
		}
		return false
	}

}