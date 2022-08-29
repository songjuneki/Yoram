package com.sjk.yoram.model.ui.listener

import android.widget.RadioGroup

interface RadioItemSelectedListener {
    fun onChange(view: RadioGroup, item: String)
}