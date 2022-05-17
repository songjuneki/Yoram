package com.sjk.yoram.Model

import java.text.SimpleDateFormat

data class LoginCheck(
    val id: Int,
    val pw: String,
    val time: String
) {
    constructor(id: Int, pw: String): this(id, pw, SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(System.currentTimeMillis()))
}
