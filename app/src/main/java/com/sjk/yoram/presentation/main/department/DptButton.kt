package com.sjk.yoram.presentation.main.department

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