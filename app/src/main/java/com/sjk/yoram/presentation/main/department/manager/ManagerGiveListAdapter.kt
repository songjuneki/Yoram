package com.sjk.yoram.presentation.main.department.manager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sjk.yoram.R
import com.sjk.yoram.data.entity.Give
import com.sjk.yoram.databinding.ListMyGiveItemBinding
import com.sjk.yoram.presentation.common.model.GiveListItem
import java.text.DecimalFormat

class ManagerGiveListAdapter(private val clickListener: GiveItemClickListener): ListAdapter<Give, ManagerGiveListAdapter.ViewHolder>(
    diffUtil
) {
    interface GiveItemClickListener {
        fun onClick(give: Give)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ListMyGiveItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_my_give_item, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ListMyGiveItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Give) {
            val format = DecimalFormat("###,###")
            binding.give = GiveListItem(item.name, item.date, format.format(item.amount))
            binding.root.setOnClickListener { clickListener.onClick(item) }
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<Give>() {
            override fun areItemsTheSame(oldItem: Give, newItem: Give): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Give, newItem: Give): Boolean = oldItem == newItem
        }
    }
}