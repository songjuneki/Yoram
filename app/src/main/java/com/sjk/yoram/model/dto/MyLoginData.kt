package com.sjk.yoram.model.dto

import java.math.BigInteger

data class MyLoginData(
    var id: Int,
    var avatar: String,
    var name: String,
    var fname: String,
    var lname: String,
    var sex: Boolean,
    var dptment: Int,
    var dptname: String,
    var dptment_parent: Int,
    var dptment_parent_name: String,
    var position: Int,
    var permission: Int,
    var attend_cnt: Int,
) {
    constructor(): this(-1, "", "익명", "익", "명", true, -1, "성도",-1, "", 0, 0, 0)
    fun getAvatarURL(): String {
        return "http://3.39.51.49:8080/api/avatar?id=$id"
    }
}