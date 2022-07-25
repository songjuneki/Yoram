package com.sjk.yoram.model

import com.google.android.material.textfield.TextInputLayout

interface TextInputChanged {
    fun afterTextChanged(view: TextInputLayout, input: String)
}