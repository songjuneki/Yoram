package com.sjk.yoram.Model

data class DptButton (
    val type: DptButtonType,
    val name: String )

enum class DptButtonType {
    DEPARTMENT,
    POSITION,
    NAME
}