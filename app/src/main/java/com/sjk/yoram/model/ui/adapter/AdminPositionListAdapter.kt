package com.sjk.yoram.model.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sjk.yoram.R
import com.sjk.yoram.databinding.ListAdminDepartmentItemBinding
import com.sjk.yoram.model.dto.Position
import com.sjk.yoram.model.ui.listener.AdminDepartmentClickListener

class AdminPositionListAdapter(private val clickListener: AdminDepartmentClickListener, private val currentCode: Int = 0): ListAdapter<Position, AdminPositionListAdapter.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ListAdminDepartmentItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
        R.layout.list_admin_department_item,
        parent,
        false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return currentList.count { it.cat == currentCode }
    }

    override fun getItem(position: Int): Position {
        return currentList.filter { it.cat == currentCode }[position]
    }


    inner class ViewHolder(private val binding: ListAdminDepartmentItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Position) {
            var isExpanded = false
            binding.isExpanded = isExpanded

            val subAdapter = AdminPositionListAdapter(this@AdminPositionListAdapter.clickListener, item.code)

            var listCount = currentList.count { it.cat == item.code }
            binding.listAdminDepartmentItemRootName.text = item.name
            binding.listAdminDepartmentItemSub.adapter = subAdapter
            subAdapter.submitList(currentList)

            binding.listAdminDepartmentItemRootLayout.setOnClickListener {
                isExpanded = !isExpanded
                binding.isExpanded = isExpanded

                if (listCount < 1)
                    binding.listAdminDepartmentItemSubEmpty.visibility = if (isExpanded) View.VISIBLE else View.GONE
                else
                    binding.listAdminDepartmentItemSubEmpty.visibility = View.GONE
            }


            binding.listAdminDepartmentItemRootEdit.setOnClickListener {
                this@AdminPositionListAdapter.clickListener.onEditClick(item.code)
            }
            binding.listAdminDepartmentItemRootAdd.setOnClickListener {
                this@AdminPositionListAdapter.clickListener.onAddClick(item.code)
            }
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<Position>() {
            override fun areItemsTheSame(oldItem: Position, newItem: Position): Boolean = oldItem.code == newItem.code && oldItem.name == newItem.name && oldItem.cat == newItem.cat
            override fun areContentsTheSame(oldItem: Position, newItem: Position): Boolean = oldItem == newItem
        }
    }
}