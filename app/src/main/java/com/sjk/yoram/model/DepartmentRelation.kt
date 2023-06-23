package com.sjk.yoram.model

data class DepartmentRelation(
    val parentList: MutableSet<Int>,
    val childList: MutableSet<Int>
)