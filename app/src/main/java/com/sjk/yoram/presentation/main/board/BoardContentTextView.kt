package com.sjk.yoram.presentation.main.board

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout.LayoutParams
import com.sjk.yoram.R

@SuppressLint("RestrictedApi")
class BoardContentTextView: androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context, text: String): super(context) {
        this.text = text
        this.setTextColor(context.getColor(R.color.xd_light_title))
        this.setAutoSizeTextTypeUniformWithConfiguration(12, 32, 2, TypedValue.COMPLEX_UNIT_SP)
        this.typeface = resources.getFont(R.font.pretendard_regular)
        this.setLineSpacing(0f, 1.3f)
        this.includeFontPadding = false
        this.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply { gravity = Gravity.START }
        this.setTextIsSelectable(true)
    }
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)
}