package com.ls.entertainment.securitylocker.ui.unlock


import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.andrognito.pinlockview.IndicatorDots
import com.andrognito.pinlockview.PinLockListener
import com.entertainment.basemvvmproject.base.BaseActivity
import com.entertainment.basemvvmproject.utils.gone
import com.entertainment.basemvvmproject.utils.visible
import com.ls.entertainment.securitylocker.MainActivity
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.ads.AdManager
import com.ls.entertainment.securitylocker.databinding.ActivityLockBinding
import com.ls.entertainment.securitylocker.ui.splash.SplashActivity
import com.ls.entertainment.securitylocker.utils.GlideHelper
import com.ls.entertainment.securitylocker.utils.LogUtils
import com.ls.entertainment.securitylocker.utils.RemoteConfig
import com.ls.entertainment.securitylocker.utils.SharePreferenceUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class UnlockActivity : BaseActivity<ActivityLockBinding, UnLockViewModel>() {
	
	private val viewModel: UnLockViewModel by viewModels()
	
	override fun getVM() = viewModel
	
	override val layoutId = R.layout.activity_lock
	
	var typePass = TYPE_UNLOCK_PASS
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		typePass = intent.getIntExtra(KEY_TYPE_PASS, TYPE_UNLOCK_PASS)
		loadNativeAds()
		initBackGround()
		initLockView()
		bindingAction()
	}
	
	private fun loadNativeAds() {
		if (RemoteConfig.commonConfig.supportNativeInLock) {
			AdManager.loadNativeAdInLock(binding.nativeAdView)
		}
	}
	
	private fun initBackGround() {
		val path = SharePreferenceUtils.getInstance().pathImageLock
		if (!path.isNullOrEmpty()) GlideHelper.load(binding.ivBackground, path)
	}
	
	private val mPinLockListener: PinLockListener = object : PinLockListener {
		override fun onComplete(pin: String) {
			LogUtils.logCustomMessage("Pin complete: $pin")
		}
		
		override fun onEmpty() {
			LogUtils.logCustomMessage("Pin empty")
		}
		
		override fun onPinChange(pinLength: Int, intermediatePin: String) {
			LogUtils.logCustomMessage("Pin changed, new length $pinLength with intermediate pin $intermediatePin")
		}
	}
	
	private fun initLockView() {
		
		binding.patternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT) // Set the current viee more
		
		//pin lock
		binding.patternPinLockView.attachIndicatorDots(binding.indicatorDots)
		binding.patternPinLockView.setPinLockListener(mPinLockListener)
		//mPinLockView.setCustomKeySet(new int[]{2, 3, 1, 5, 9, 6, 7, 0, 8, 4});
		binding.patternPinLockView.enableLayoutShuffling()
		
		binding.patternPinLockView.pinLength = 4
		binding.patternPinLockView.textColor = ContextCompat.getColor(this, R.color.white)
		
		binding.indicatorDots.indicatorType = IndicatorDots.IndicatorType.FILL
	}
	
	private fun bindingAction() {
		binding.patternLockView.addPatternLockListener(object : PatternLockViewListener {
			override fun onStarted() {
			
			}
			
			override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {
			
			}
			
			override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
				if (typePass == TYPE_SETUP_PASS) {
					SharePreferenceUtils.getInstance().passWord =
						PatternLockUtils.patternToString(
							binding.patternLockView, pattern
						)
					SharePreferenceUtils.getInstance().isSetupPass = true
					startActivity(Intent(this@UnlockActivity, MainActivity::class.java))
					finish()
				} else {
					if (PatternLockUtils.patternToString(
							binding.patternLockView, pattern
						) == SharePreferenceUtils.getInstance().passWord
					) {
						finish()
					}
				}
				binding.patternLockView.clearPattern()
			}
			
			override fun onCleared() {
			
			}
		})
		
		binding.ivSetting.setOnClickListener {
			val popupMenu = PopupMenu(this@UnlockActivity, binding.ivSetting)
			popupMenu.menuInflater.inflate(R.menu.menu_lock, popupMenu.menu)
			popupMenu.setOnMenuItemClickListener { menuItem ->
				when (menuItem.itemId) {
					R.id.menu_lock_app -> {
						val intent = Intent(this, SplashActivity::class.java)
						intent.putExtra(
							SplashActivity.KEY_TYPE_OPTIMIZE, SplashActivity.TYPE_FROM_UNLOCK
						)
						intent.putExtra(
							SplashActivity.KEY_ACTION_MENU_LOCK, SplashActivity.ACTION_MENU_LOCK_APP
						)
						intent.flags =
							Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
						startActivity(intent)
						finish()
					}
					
					R.id.menu_change_theme -> {
						val intent = Intent(this, SplashActivity::class.java)
						intent.putExtra(
							SplashActivity.KEY_TYPE_OPTIMIZE, SplashActivity.TYPE_FROM_UNLOCK
						)
						intent.putExtra(
							SplashActivity.KEY_ACTION_MENU_LOCK,
							SplashActivity.ACTION_MENU_CHANGE_THEME
						)
						intent.flags =
							Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
						startActivity(intent)
						finish()
					}
					
					R.id.menu_setting_app -> {
						val intent = Intent(this, SplashActivity::class.java)
						intent.putExtra(
							SplashActivity.KEY_TYPE_OPTIMIZE, SplashActivity.TYPE_FROM_UNLOCK
						)
						intent.putExtra(
							SplashActivity.KEY_ACTION_MENU_LOCK, SplashActivity.ACTION_MENU_SETTING
						)
						intent.flags =
							Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
						startActivity(intent)
						finish()
					}
				}
				true
			}
			popupMenu.show()
		}
		if (typePass == TYPE_SETUP_PASS) {
			binding.ivSetting.gone()
			binding.tvHelp.text = getString(R.string.set_up_pass)
		} else {
			binding.ivSetting.visible()
			viewModel.viewModelScope.launch {
				delay(1500)
				binding.ivSetting.performClick()
			}
		}
		
	}
	
	override fun onBackPressed() {
		showToast("Please enter password")
	}
	
	override fun onPause() {
		super.onPause()
		finish()
	}
	
	override fun onDestroy() {
		super.onDestroy()
		SharePreferenceUtils.getInstance().canShowOpenAd = false
	}
	
	override fun onKeyDown(key_code: Int, key_event: KeyEvent?): Boolean {
		if (key_code == KeyEvent.KEYCODE_BACK) {
			super.onKeyDown(key_code, key_event)
			return true
		}
		return false
	}
	
	companion object {
		const val KEY_TYPE_PASS = "key_type_pass"
		const val TYPE_SETUP_PASS = 65643
		const val TYPE_UNLOCK_PASS = 435
	}
	
}