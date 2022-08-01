package com.sjk.yoram.model

import com.sjk.yoram.model.dto.MyLoginData

data class Card (
    val type: CardType,
    val data: cardData? ) {
    constructor(type: CardType) : this(type, null)
}

enum class CardType {
    HOME_BANNER,
    HOME_DEPARTMENT,
    HOME_ID,
    HOME_BOARD
}

sealed class cardData

open class BannerData(val imgUrl: Any, val url: String): cardData()
open class userData(val user: MyLoginData): cardData()