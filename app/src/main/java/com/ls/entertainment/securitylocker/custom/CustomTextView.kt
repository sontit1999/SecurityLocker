package com.ls.entertainment.securitylocker.custom

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CustomTextView : AppCompatTextView {
    constructor(context: Context) : super(context) {
        applyFont()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        applyFont()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        applyFont()
    }

    private fun applyFont() {
        val tf = Typeface.createFromAsset(this.context.assets, "opensan.ttf")
        typeface = tf
    }
}