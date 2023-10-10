package com.sjk.yoram.model.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sjk.yoram.R
import com.sjk.yoram.databinding.ListAdminBoardItemBinding
import com.sjk.yoram.model.dto.ReservedBoardCategory
import com.sjk.yoram.model.ui.listener.AdminBoardItemTouchCallback
import java.util.*

class AdminReserveBoardListAdapter(
    private val onClick: (ReservedBoardCategory, Int) -> Unit,
    private val onMove: (List<ReservedBoardCategory>) -> Unit
)
    : ListAdapter<ReservedBoardCategory, RecyclerView.ViewHolder>(diffUtil),
    AdminBoardItemTouchCallback.OnBoardItemMoveListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ListAdminBoardItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_admin_board_item,
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(getItem(position), position)
    }

    inner class ViewHolder(private val binding: ListAdminBoardItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.updateLayoutParams {
                this.width = LayoutParams.WRAP_CONTENT
            }
        }
        fun bind(item: ReservedBoardCategory, pos: Int) {
            binding.listAdminBoardTitle.text = item.name

            binding.root.setOnClickListener {
                onClick(item, pos)
            }
        }
    }

    override fun itemMoved(from: Int, to: Int) {
        val list = currentList.toMutableList()
        Collections.swap(list, from, to)
        submitList(list)
    }

    override fun touchEnd() {
        val list = currentList.mapIndexed { i, item ->
            item.copy(order = i)
        }
        onMove(list)
        submitList(list)
    }


    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<ReservedBoardCategory>() {
            override fun areContentsTheSame(
                oldItem: ReservedBoardCategory,
                newItem: ReservedBoardCategory
            ): Boolean = oldItem == newItem

            override fun areItemsTheSame(
                oldItem: ReservedBoardCategory,
                newItem: ReservedBoardCategory
            ): Boolean = oldItem.id == newItem.id
        }
    }

}