package com.sjk.yoram.model

data class NewUser(
    var name: String,
    var fname: String,
    var lname: String,
    var sex: SexState,
    var birth: String,
    var pw: String,
    var phone: String,
    var tel: String,
    var address: String,
    var address_more: String,
    var car: String
) {
    constructor(): this("", "","",SexState.NONE, "","", "", "", "", "", "")
    fun checkInput(): Boolean {
        return name.isNotEmpty() && pw.isNotEmpty() && sex != SexState.NONE && birth.isNotEmpty()
    }
}