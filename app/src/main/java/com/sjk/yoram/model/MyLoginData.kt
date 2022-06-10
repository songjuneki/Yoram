package com.sjk.yoram.model

data class MyLoginData(
    var id: Int,
    var avatar: String,
    var fname: String,
    var lname: String,
    var sex: Boolean,
    var department: Int,
    var dptname: String,
    var position: Int,
    var permission: Int
) {
    constructor(): this(-1, "", "익", "명", true, -1, "성도",-1, 0)
    fun getAvatarURL(): String {
        return "http://3.39.51.49:8080/api/avatar?id=$id"
    }
}