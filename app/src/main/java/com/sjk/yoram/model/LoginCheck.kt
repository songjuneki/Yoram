package com.sjk.yoram.model

import java.text.SimpleDateFormat
import java.util.Locale

data class LoginCheck(
    val id: Int,
    val name: String,
    val pw: String,
    val bd: String,
    val time: String
) {
    constructor(id: Int, pw: String): this(id, "", pw, "", SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.KOREA).format(System.currentTimeMillis()))
    constructor(name: String, pw: String): this(-1, name, pw, "", SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.KOREA).format(System.currentTimeMillis()))
    constructor(name: String, pw: String, bd: String): this(-1, name, pw, bd, SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.KOREA).format(System.currentTimeMillis()))
    constructor(id: Int, pw: String, bd: String): this(id, "", pw, bd, SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.KOREA).format(System.currentTimeMillis()))
    constructor(id: Int, name: String, pw: String, bd: String): this(id, name, pw, bd, SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.KOREA).format(System.currentTimeMillis()))
}
