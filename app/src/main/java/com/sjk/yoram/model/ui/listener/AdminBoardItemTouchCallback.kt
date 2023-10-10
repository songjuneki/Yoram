package com.sjk.yoram.model.ui.listener

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminBoardItemTouchCallback(private val listener: OnBoardItemMoveListener): ItemTouchHelper.Callback() {
    interface OnBoardItemMoveListener {
        fun itemMoved(from: Int, to: Int)
        fun touchEnd()
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return when (recyclerView.layoutManager) {
            is GridLayoutManager -> makeMovementFlags(
                ItemTouchHelper.UP
                        or ItemTouchHelper.DOWN
                        or ItemTouchHelper.LEFT
                        or ItemTouchHelper.RIGHT,
                0
            )
            is LinearLayoutManager -> makeMovementFlags(
                ItemTouchHelper.LEFT
                        or ItemTouchHelper.RIGHT,
                0
            )
            else -> -1
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        listener.itemMoved(viewHolder.absoluteAdapterPosition, target.absoluteAdapterPosition)
        return true
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        listener.touchEnd()
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) { }

    override fun isItemViewSwipeEnabled(): Boolean = false

}