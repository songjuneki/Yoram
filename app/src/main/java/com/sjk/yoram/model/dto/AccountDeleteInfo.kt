package com.sjk.yoram.model.dto

data class AccountDeleteInfo(
    val id: Int,
    val bd: String,
    val phone: String,
    val pw: String,
    val delete_date: String
)
