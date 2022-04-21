package com.sjk.yoram.Model

import android.view.View
import android.widget.ImageView

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

open class BannerData(val imgUrl: String, val url: String): cardData()
open class userData(val name: String, val department: Department): cardData()