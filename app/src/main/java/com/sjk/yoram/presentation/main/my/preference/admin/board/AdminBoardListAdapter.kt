package com.sjk.yoram.presentation.main.my.preference.admin.board

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sjk.yoram.R
import com.sjk.yoram.data.entity.BoardCategory
import com.sjk.yoram.databinding.ListAdminBoardItemBinding

class AdminBoardListAdapter(
    private val onClick: (BoardCategory, Int) -> Unit,
    private val onMove: (List<BoardCategory>) -> Unit
)
    : ListAdapter<BoardCategory, RecyclerView.ViewHolder>(diffUtil),
    AdminBoardItemTouchCallback.OnBoardItemMoveListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ListAdminBoardItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_admin_board_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(getItem(position), position)
    }

    inner class ViewHolder(private val binding: ListAdminBoardItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.updateLayoutParams {
                this.width = LayoutParams.MATCH_PARENT
            }
        }
        fun bind(item: BoardCategory, pos: Int) {
            binding.listAdminBoardTitle.text = item.name

            binding.root.setOnClickListener {
                onClick(item, pos)
            }
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<BoardCategory>() {
            override fun areContentsTheSame(
                oldItem: BoardCategory,
                newItem: BoardCategory
            ): Boolean = oldItem == newItem

            override fun areItemsTheSame(oldItem: BoardCategory, newItem: BoardCategory): Boolean = oldItem.id == newItem.id
        }
    }

    override fun itemMoved(from: Int, to: Int) {
        val list = currentList.toMutableList()
        list.add(to, list.removeAt(from))
        submitList(list)
    }

    override fun touchEnd() {
        onMove(currentList)
    }

}