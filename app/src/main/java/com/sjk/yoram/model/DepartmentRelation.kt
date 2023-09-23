package com.sjk.yoram.model

data class DepartmentRelation(
    val parentList: MutableSet<Int> = mutableSetOf(),
    val childList: MutableSet<Int> = mutableSetOf()
)