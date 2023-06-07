package com.sjk.yoram.model

data class DptButton (
    val type: DptButtonType,
    val name: String,
    val code: Int? )

enum class DptButtonType {
    NAME,
    DEPARTMENT,
    POSITION
}

data class SimpleDpt(
    val code: Int,
    val name: String
)