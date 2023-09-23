package com.sjk.yoram.model

data class ExpandableDepartment(
    val code: Int,
    val name: String,
    val parent: Int,
    var count: Int = 0,
    var isExpanded: Boolean = true,
    var isHide: Boolean = false
) {
    constructor(node: DepartmentNode): this(node.code, node.name, node.parent, node.count, node.isExpanded, !node.isExpanded)
}
