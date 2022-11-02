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
) {
    companion object {
        const val URL = "http://3.39.51.49:8080/api/banner?id="
    }
    fun isEqual(other: Banner?): Boolean {
        val flag = this.title == other!!.title
                && this.order == other!!.order
                && this.link == other!!.link
                && this.expire == other!!.expire
                && this.show == other!!.show

        if (other == null) return false
        return flag
    }
}
