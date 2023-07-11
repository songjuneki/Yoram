package com.sjk.yoram.model.ui.listener

import com.sjk.yoram.model.dto.Board

interface BoardClickListener {
    fun onClick(board: Board)
}