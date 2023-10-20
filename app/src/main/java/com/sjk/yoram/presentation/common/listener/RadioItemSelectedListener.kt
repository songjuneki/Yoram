package com.sjk.yoram.presentation.common.listener

import android.widget.RadioGroup

interface RadioItemSelectedListener {
    fun onChange(view: RadioGroup, item: String)
}