package com.sjk.yoram.data.entity

data class NewUserForAdmin(
    val app_agree_date: String,
    val birth: String,
    val id: Int,
    val name: String,
    val phone: String,
    val sex: Boolean
)