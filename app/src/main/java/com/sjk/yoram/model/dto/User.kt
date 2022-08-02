package com.sjk.yoram.model.dto

data class User(
    val id: Int,
    val name: String,
    val Fname: String,
    val Lname: String,
    val pw: String,
    val sex: Boolean,
    val birth: String,
    val position: Int,
    val department: Int,
    val phone: String,
    val tel: String,
    val carno: String,
    val family: Int,
    val address: String,
    val address_more: String,
    val permission: Int,
    val avatar: String
)