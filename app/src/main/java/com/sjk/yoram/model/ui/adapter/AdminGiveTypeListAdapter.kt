package com.sjk.yoram.model.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sjk.yoram.R
import com.sjk.yoram.databinding.ListAdminWorshipItemBinding
import com.sjk.yoram.model.dto.GiveType
import com.sjk.yoram.model.dto.WorshipType
import com.sjk.yoram.model.ui.listener.AdminGiveTypeClickListener

class AdminGiveTypeListAdapter(private val clickListener: AdminGiveTypeClickListener): ListAdapter<GiveType, AdminGiveTypeListAdapter.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ListAdminWorshipItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
        R.layout.list_admin_worship_item,
        parent,
        false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class ViewHolder(private val binding: ListAdminWorshipItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GiveType) {
            val data = WorshipType(item.type, item.name)
            binding.worship = data
            binding.listAdminWorshipItemNumber.text = "${bindingAdapterPosition+1}."
            binding.root.setOnClickListener {
                clickListener.onClick(bindingAdapterPosition)
            }
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<GiveType>() {
            override fun areItemsTheSame(oldItem: GiveType, newItem: GiveType): Boolean = oldItem.type == newItem.type
            override fun areContentsTheSame(oldItem: GiveType, newItem: GiveType): Boolean = oldItem == newItem
        }
    }
}