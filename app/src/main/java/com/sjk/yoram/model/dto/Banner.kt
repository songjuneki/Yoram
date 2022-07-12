package com.sjk.yoram.model.dto

data class Banner(
    val id: Int,
    var title: String,
    val owner: Int,
    val path: String,
    var link: String,
    var order: Int,
    val create_date: String,
    val create_time: String,
    var expire: String,
    var show: Boolean
)
