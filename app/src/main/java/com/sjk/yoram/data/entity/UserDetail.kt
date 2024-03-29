package com.sjk.yoram.data.entity

data class UserDetail(
    var id: Int,
    var sex: Boolean,
    var name: String,
    var position: Int,
    var position_name: String,
    var department: Int,
    var department_name: String,
    var birth: String,
    var phone: String,
    var tel: String,
    var address: String,
    var address_more: String,
    var car: String,
    var avatar: String,
    var permission: Int,
    var app_agree_date: String,
    var privacy_agree_date: String
)
