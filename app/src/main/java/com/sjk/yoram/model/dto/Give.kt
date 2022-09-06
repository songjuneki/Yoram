package com.sjk.yoram.model.dto

import java.math.BigInteger

data class Give(
    val uid: Int,
    val give_type: Int,
    val name: String,
    val worship_type: Int,
    val date: String,
    val amount: BigInteger
)
