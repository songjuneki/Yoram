package com.sjk.yoram.model

import com.sjk.yoram.model.dto.SimpleUser

data class DepartmentNode(
    val code: Int,
    val name: String,
    val parent: Int = 0,
    var child: MutableList<DepartmentNode> = mutableListOf(),
    var users: MutableList<SimpleUser> = mutableListOf(),
    var isExpanded: Boolean = true,
    var count: Int = 0
)