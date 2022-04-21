package com.sjk.yoram.Model.dto

data class User(
    val address: String,
    val birth: String,
    val carno: String,
    val department: Int,
    val id: Int,
    val Fname: String,
    val Lname: String,
    val permission: Int,
    val family: Int,
    val position: Int,
    val pw: String,
    val sex: Boolean,
    val tel1: String,
    val tel2: String,
    val tel3: String
)