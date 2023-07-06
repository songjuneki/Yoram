package com.sjk.yoram.model.dto

data class Board(
    val board_id: Int,
    val category_id: Int,
    val category_name: String,
    val owner_user_id: Int,
    val owner_user_name: String,
    val board_option_script: String,
    val board_option_script_date: String,
    val board_title: String,
    val board_content: String,
    val board_media_list: List<BoardMedia>,
    val board_date: String,
    val board_update: String,
    val board_view_count: Int,
    val board_comment_count: Int,
    val board_like_count: Int
)