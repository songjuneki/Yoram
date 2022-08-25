package com.sjk.yoram.model.dto

data class MyLoginData(
    var id: Int,
    var avatar: String,
    var name: String,
    var sex: Boolean,
    var department: Int,
    var department_name: String,
    var department_parent: Int,
    var department_parent_name: String,
    var position: Int,
    var position_name: String,
    var permission: Int,
    var attend_cnt: Int,
) {
    constructor(): this(-1, "", "익명", true, -1, "성도",-1, "성도", -1, "성도", 0, 0)
}