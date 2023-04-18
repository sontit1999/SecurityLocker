package com.ls.entertainment.securitylocker

import android.os.Bundle
import android.view.KeyEvent
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.andrognito.patternlockview.utils.ResourceUtils
import com.example.demoandroidrikkei.base.ui.BaseActivityNotRequireViewModel
import com.ls.entertainment.securitylocker.databinding.ActivityLockBinding

class UnlockActivity : BaseActivityNotRequireViewModel<ActivityLockBinding>() {

	override val layoutId = R.layout.activity_lock

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		initLockView()
		bindingAction()
	}

	private fun initLockView() {
		binding.patternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT) // Set the current viee more

		/*binding.patternLockView.isInStealthMode = true // Set the pattern in stealth mode (pattern drawing is hidden)

		binding.patternLockView.isTactileFeedbackEnabled = true // Enables vibration feedback when the pattern is drawn*/



	/*	binding.patternLockView.mPatternLockView.setDotCount(3)
		binding.patternLockView.mPatternLockView.setDotNormalSize(
			ResourceUtils.getDimensionInPx(
				this,
				R.dimen.pattern_lock_dot_size
			) as Int
		)
		mPatternLockView.setDotSelectedSize(
			ResourceUtils.getDimensionInPx(
				this,
				R.dimen.pattern_lock_dot_selected_size
			) as Int
		)
		mPatternLockView.setPathWidth(
			ResourceUtils.getDimensionInPx(
				this,
				R.dimen.pattern_lock_path_width
			) as Int
		)*/
		/*binding.patternLockView.isAspectRatioEnabled = true
		binding.patternLockView.aspectRatio = PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS
		binding.patternLockView.normalStateColor = ResourceUtils.getColor(this, R.color.white)
		*//*binding.patternLockView.setCorrectStateColor(ResourceUtils.getColor(this, R.color.primary))
		binding.patternLockView.setWrongStateColor(ResourceUtils.getColor(this, R.color.pomegranate))*//*
		binding.patternLockView.dotAnimationDuration = 150
		binding.patternLockView.pathEndAnimationDuration = 100*/
	}

	private fun bindingAction() {
		binding.patternLockView.addPatternLockListener(object : PatternLockViewListener {
			override fun onStarted() {

			}

			override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {

			}

			override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
				if(PatternLockUtils.patternToString(binding.patternLockView, pattern) == "0124678"){
					finish()
				}
				binding.patternLockView.clearPattern()
			}

			override fun onCleared() {

			}
		})
	}

	override fun onBackPressed() {
		showToast("Please enter password")
	}

	override fun onPause() {
		super.onPause()
		finish()
	}

	override fun onKeyDown(key_code: Int, key_event: KeyEvent?): Boolean {
		if (key_code == KeyEvent.KEYCODE_BACK) {
			super.onKeyDown(key_code, key_event)
			return true
		}
		return false
	}

}