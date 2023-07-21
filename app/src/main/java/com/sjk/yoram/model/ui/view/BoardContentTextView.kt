package com.sjk.yoram.model.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout.LayoutParams
import com.sjk.yoram.R

@SuppressLint("RestrictedApi")
class BoardContentTextView: androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    init {
        this.setTextColor(context.getColor(R.color.xd_light_title))
        this.setAutoSizeTextTypeUniformWithConfiguration(8, 32, 2, TypedValue.COMPLEX_UNIT_SP)
        this.typeface = resources.getFont(R.font.pretendard_regular)
        this.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply { gravity = Gravity.START }
        this.setTextIsSelectable(true)
    }
}