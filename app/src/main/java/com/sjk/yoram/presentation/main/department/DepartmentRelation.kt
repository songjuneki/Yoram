package com.sjk.yoram.presentation.main.department

data class DepartmentRelation(
    val parentList: MutableSet<Int> = mutableSetOf(),
    val childList: MutableSet<Int> = mutableSetOf()
)