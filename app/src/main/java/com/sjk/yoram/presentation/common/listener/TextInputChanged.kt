package com.sjk.yoram.presentation.common.listener

import com.google.android.material.textfield.TextInputLayout

interface TextInputChanged {
    fun afterTextChanged(view: TextInputLayout, input: String)
}