package com.sjk.yoram.model.dto

data class ReservedBoardCategory(
    val id: Long,
    val name: String,
    val board_id: Int,
    val board_type: String,
    val order: Int
) {
    fun toBoardCategory(): BoardCategory {
        return BoardCategory(id, name, BoardCategoryType.valueOf(board_type))
    }
}
