package com.sjk.yoram.Model

data class NewUser(
    var fname: String,
    var lname: String,
    var sex: Boolean,
    var birth: String,
    var pw: String,
    var tel1: String,
    var tel2: String,
    var address: String,
    var carno: String
) {
    constructor(): this("","",true, "","", "", "", "", "")
}