package com.sjk.yoram.model.dto

data class UserDetail(
    val id: Int,
    val sex: Boolean,
    val name: String,
    val position: Int,
    val position_name: String,
    val department: Int,
    val department_name: String,
    val birth: String,
    val phone: String,
    val tel: String,
    val address: String,
    val address_more: String,
    val car: String,
    val avatar: String
)
