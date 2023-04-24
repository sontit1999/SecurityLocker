package com.ls.entertainment.securitylocker


import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.andrognito.pinlockview.IndicatorDots
import com.andrognito.pinlockview.PinLockListener
import com.example.demoandroidrikkei.base.ui.BaseActivityNotRequireViewModel
import com.ls.entertainment.securitylocker.databinding.ActivityLockBinding
import com.ls.entertainment.securitylocker.utils.GlideHelper
import com.ls.entertainment.securitylocker.utils.LogUtils
import com.ls.entertainment.securitylocker.utils.SharePreferenceUtils


class UnlockActivity : BaseActivityNotRequireViewModel<ActivityLockBinding>() {

    override val layoutId = R.layout.activity_lock

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBackGround()
        initLockView()
        bindingAction()

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
                if (PatternLockUtils.patternToString(
                        binding.patternLockView,
                        pattern
                    ) == "0124678"
                ) {
                    finish()
                }
                binding.patternLockView.clearPattern()
            }
            
            override fun onCleared() {
            
            }
        })
        
        binding.ivSetting.setOnClickListener {
            val popupMenu = PopupMenu(this@UnlockActivity, binding.ivSetting)
            popupMenu.menuInflater.inflate(R.menu.menu_main, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
                true
            }
            popupMenu.show()
        }
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