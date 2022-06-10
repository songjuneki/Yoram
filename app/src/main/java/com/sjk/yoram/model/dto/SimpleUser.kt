package com.sjk.yoram.model.dto

data class SimpleUser(
    val id: Int,
    val fname: String,
    val lname: String,
    val sex: Boolean,
    val position: Int,
    val department: Int
) {
    constructor(user: User): this(user.id, user.fname, user.lname, user.sex, user.position, user.department)
}
