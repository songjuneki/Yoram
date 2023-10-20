package com.sjk.yoram.util

import android.os.Parcelable
import com.sjk.yoram.data.entity.ReservedBoardCategory
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ParcelableReservedBoardCategory (
        val id: Long,
        val name: String,
        val board_id: Int,
        val board_type: String,
        val order: Int
): Parcelable {
        fun toPure(): ReservedBoardCategory = ReservedBoardCategory(id, name, board_id, board_type, order)
}

fun ReservedBoardCategory.parcelize(): ParcelableReservedBoardCategory = ParcelableReservedBoardCategory(id, name, board_id, board_type, order)