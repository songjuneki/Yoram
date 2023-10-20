package com.sjk.yoram.presentation.main.my.give

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sjk.yoram.R
import com.sjk.yoram.databinding.ListMyGiveItemBinding
import com.sjk.yoram.presentation.common.model.GiveListItem

class GiveListAdapter(): ListAdapter<GiveListItem, GiveListAdapter.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ListMyGiveItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_my_give_item, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ListMyGiveItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GiveListItem) {
            binding.give = item
            binding.root.isClickable = false
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<GiveListItem>() {
            override fun areItemsTheSame(oldItem: GiveListItem, newItem: GiveListItem): Boolean {
                return oldItem.amount == newItem.amount && oldItem.date == newItem.date && oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: GiveListItem, newItem: GiveListItem): Boolean {
                return oldItem == newItem
            }

        }
    }
}