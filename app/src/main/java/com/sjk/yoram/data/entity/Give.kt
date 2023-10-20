package com.sjk.yoram.data.entity

import java.math.BigInteger

data class Give(
    var id: Int,
    var uid: Int,
    var give_type: Int,
    var name: String,
    var worship_type: Int,
    var date: String,
    var amount: BigInteger
)
