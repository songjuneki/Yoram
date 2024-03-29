package com.sjk.yoram.data.entity

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
    var app_agree_date: String,
    var privacy_agree_date: String
) {
    constructor(): this(-1, "", "익명", true, -1, "성도",-1, "성도", -1, "성도", 0, 0, "", "")
}