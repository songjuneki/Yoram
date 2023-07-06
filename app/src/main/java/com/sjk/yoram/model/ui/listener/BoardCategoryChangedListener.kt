package com.sjk.yoram.model.ui.listener

import com.sjk.yoram.model.dto.ReservedBoardCategory

interface BoardCategoryChangedListener {
    fun onChanged(changedCategory: ReservedBoardCategory)
}