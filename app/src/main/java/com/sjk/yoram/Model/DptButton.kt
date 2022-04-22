package com.sjk.yoram.Model

data class DptButton (
    val type: DptButtonType,
    val name: String,
    val code: Int? )

enum class DptButtonType {
    DEPARTMENT,
    POSITION,
    NAME
}

data class SimpleDpt(
    val code: Int,
    val name: String
)