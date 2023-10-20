package com.sjk.yoram.presentation.main.my.preference.admin.banner

import androidx.recyclerview.widget.RecyclerView

interface AdminBannerItemTouchHelperListener {
    fun onItemMove(from_position: Int, to_position: Int): Boolean
    fun onItemSwipe(position: Int)
    fun onLeftClick(position: Int, viewHolder: RecyclerView.ViewHolder?)
    fun onRightClick(position: Int, viewHolder: RecyclerView.ViewHolder?)
}