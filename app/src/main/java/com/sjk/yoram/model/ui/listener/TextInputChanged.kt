package com.sjk.yoram.model.ui.listener

import com.google.android.material.textfield.TextInputLayout

interface TextInputChanged {
    fun afterTextChanged(view: TextInputLayout, input: String)
}