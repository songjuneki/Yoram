package com.sjk.yoram.data.entity

data class ReservedBoardCategory(
    val id: Long,
    val name: String,
    val board_id: Int,
    val board_type: String,
    val order: Int
) {
    fun toBoardCategory(): BoardCategory = BoardCategory(board_id.toLong(), name, BoardCategoryType.valueOf(board_type))
}
