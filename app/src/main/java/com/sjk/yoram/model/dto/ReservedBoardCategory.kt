package com.sjk.yoram.model.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReservedBoardCategory(
    val id: Long,
    val name: String,
    val board_id: Int,
    val board_type: String,
    val order: Int
) : Parcelable {
    fun toBoardCategory(): BoardCategory {
        return BoardCategory(board_id.toLong(), name, BoardCategoryType.valueOf(board_type))
    }
}
