package com.sjk.yoram.model

import java.text.SimpleDateFormat

data class LoginCheck(
    val id: Int,
    val pw: String,
    val bd: String,
    val time: String
) {
    constructor(id: Int, pw: String): this(id, pw, "", SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(System.currentTimeMillis()))
    constructor(id: Int, pw: String, bd: String): this(id, pw, bd, SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(System.currentTimeMillis()))
}
