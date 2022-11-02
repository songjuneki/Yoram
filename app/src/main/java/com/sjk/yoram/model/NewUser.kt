package com.sjk.yoram.model

data class NewUser(
    var name: String,
    var sex: Boolean,
    var birth: String,
    var pw: String,
    var phone: String,
    var tel: String,
    var address: String,
    var address_more: String,
    var car: String
) {
    constructor(): this("", true, "","", "", "", "", "", "")
}