package com.sjk.yoram.presentation.common.model

data class NewUser(
    var name: String,
    var sex: Boolean,
    var birth: String,
    var pw: String,
    var phone: String,
    var tel: String,
    var address: String,
    var address_more: String,
    var car: String,
    var app_agree_date: String,
    var privacy_agree_date: String
) {
    constructor(): this("", true, "","", "", "", "", "", "", "", "")
}