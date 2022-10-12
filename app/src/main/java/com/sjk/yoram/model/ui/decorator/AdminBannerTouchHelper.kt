package com.sjk.yoram.model.ui.decorator

import android.graphics.*
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.sjk.yoram.R
import com.sjk.yoram.model.ui.adapter.AdminBannerListAdapter
import com.sjk.yoram.model.ui.listener.AdminBannerItemTouchHelperListener

class AdminBannerTouchHelper(private val listener: AdminBannerItemTouchHelperListener): ItemTouchHelper.Callback() {
    private var currentPosition: Int? = null
    private var previousPosition: Int? = null
    private var currentDx = 0f
    private var clamp = 200f

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val moveFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlag = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(moveFlag, swipeFlag)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return listener.onItemMove(viewHolder.absoluteAdapterPosition, target.absoluteAdapterPosition)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onItemSwipe(viewHolder.absoluteAdapterPosition)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val card = getView(viewHolder).findViewById<MaterialCardView>(R.id.list_admin_banner_card)
            val isClamped = getTag(viewHolder)
            val x = clampViewPositionHorizontal(card, dX, isClamped, isCurrentlyActive)

            currentDx = x
            getDefaultUIUtil().onDraw(c, recyclerView, card, x, dY, actionState, isCurrentlyActive)
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG)
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }


    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        currentDx = 0f
        previousPosition = viewHolder.absoluteAdapterPosition
        getDefaultUIUtil().clearView(getView(viewHolder).findViewById(R.id.list_admin_banner_card))
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        viewHolder?.let {
            currentPosition = viewHolder.bindingAdapterPosition
            getDefaultUIUtil().onSelected(getView(it).findViewById(R.id.list_admin_banner_card))
        }
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return defaultValue * 10
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        val isClamped = getTag(viewHolder)
        setTag(viewHolder, !isClamped && currentDx <= -clamp)
        return 2f
    }

    override fun isLongPressDragEnabled(): Boolean = false
    override fun isItemViewSwipeEnabled(): Boolean = true

    private fun getView(viewHolder: RecyclerView.ViewHolder): View {
        return (viewHolder as AdminBannerListAdapter.ViewHolder).itemView
    }

    private fun getTag(viewHolder: RecyclerView.ViewHolder): Boolean {
        return viewHolder.itemView.tag as? Boolean ?: false
    }

    private fun setTag(viewHolder: RecyclerView.ViewHolder, isClamped: Boolean) {
        viewHolder.itemView.tag = isClamped
    }

    private fun setClamp(clamp: Float) {
        this.clamp = clamp
    }

    private fun removePreviousClamp(recyclerView: RecyclerView) {
        if (currentPosition == previousPosition) return
        previousPosition?.let {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(it) ?: return
            getView(viewHolder).findViewById<MaterialCardView>(R.id.list_admin_banner_card).translationX = 0f
            setTag(viewHolder, false)
            previousPosition = null
        }
    }

    private fun clampViewPositionHorizontal(
        view: View,
        dX: Float,
        isClamped: Boolean,
        isCurrentlyActive: Boolean
    ): Float {
        val min: Float = -view.width.toFloat() / 5
        val max: Float = 0f

        val x = if (isClamped) {
            if (isCurrentlyActive) dX - clamp else -clamp
        } else {
            dX
        }

        return kotlin.math.min(kotlin.math.max(min, x), max)
    }


    enum class ButtonState { GONE, LEFT_VISIBLE, RIGHT_VISIBLE }
    enum class IconType { DELETE, VISIBLE_TOGGLE}
}

