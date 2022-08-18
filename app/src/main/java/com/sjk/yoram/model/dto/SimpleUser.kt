package com.sjk.yoram.model.dto

data class SimpleUser(
    val id: Int,
    val name: String,
    val sex: Boolean,
    val position: Int,
    val position_name: String,
    val department: Int,
    val department_name: String,
    val avatar: String
) {
    constructor(user: User): this(user.id, user.name, user.sex, user.position, "" ,user.department, "" ,user.avatar)
}
