package com.sjk.yoram.model.ui.adapter

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.sjk.yoram.R
import com.sjk.yoram.databinding.BoardCategoryItemBinding
import com.sjk.yoram.model.dto.ReservedBoardCategory
import com.sjk.yoram.model.ui.listener.BoardCategoryChangedListener

class BoardCategoryListAdapter(val categoryChangedListener: BoardCategoryChangedListener): ListAdapter<ReservedBoardCategory, RecyclerView.ViewHolder>(diffUtil) {
    private var currentCategoryPos: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: BoardCategoryItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.board_category_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(getItem(position), position)
    }

    inner class ViewHolder(val binding: BoardCategoryItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReservedBoardCategory, pos: Int) {
            binding.boardCategoryName.text = item.name
            (binding.root as MaterialCardView)

            if (currentCategoryPos == bindingAdapterPosition) {
                (binding.root as MaterialCardView).strokeColor = binding.root.context.getColor(R.color.xd_light_dot_indicator_enabled)
                (binding.root as MaterialCardView).setCardBackgroundColor(binding.root.context.getColor(R.color.xd_light_dot_indicator_enabled))
                binding.boardCategoryName.setTextColor(binding.root.context.getColor(R.color.xd_light_background))
            } else {
                (binding.root as MaterialCardView).strokeColor = binding.root.context.getColor(R.color.xd_light_text_hint)
                (binding.root as MaterialCardView).setCardBackgroundColor(binding.root.context.getColor(R.color.xd_light_background))
                binding.boardCategoryName.setTextColor(binding.root.context.getColor(R.color.xd_light_hint))
            }

            binding.root.setOnClickListener {
                selectCategoryChange(pos)
                categoryChangedListener.onChanged(item)
            }
        }
    }

    private fun selectCategoryChange(pos: Int) {
        if (pos == currentCategoryPos) return
        val oldPos = currentCategoryPos
        currentCategoryPos = pos
        notifyItemChanged(oldPos)
        notifyItemChanged(currentCategoryPos)
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<ReservedBoardCategory>() {
            override fun areContentsTheSame(oldItem: ReservedBoardCategory, newItem: ReservedBoardCategory): Boolean = oldItem == newItem
            override fun areItemsTheSame(oldItem: ReservedBoardCategory, newItem: ReservedBoardCategory): Boolean = oldItem.id == newItem.id
        }
    }
}