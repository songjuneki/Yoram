package com.sjk.yoram.data.entity

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
) {
    companion object {
        const val URL = "http://hyuny840501.cafe24.com:8080/api/banner?id="
    }

    fun isEqual(other: Banner?): Boolean {
        if (other == null) return false
        return (title == other.title
                && order == other.order
                && link == other.link
                && expire == other.expire
                && show == other.show)
    }
}
