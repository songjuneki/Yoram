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
import com.sjk.yoram.databinding.CardDepartmentBinding
import com.sjk.yoram.model.Department
import com.sjk.yoram.model.DepartmentSubData
import com.sjk.yoram.model.DptSubDataType
import com.sjk.yoram.model.ui.listener.DepartmentItemClickListener

class DepartmentListAdapter(private val clickListener: DepartmentItemClickListener): ListAdapter<Department, DepartmentListAdapter.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CardDepartmentBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.card_department, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder(private val binding: CardDepartmentBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Department, listener: DepartmentItemClickListener) {
            binding.cardDptTitle.text = item.name
            binding.cardDptCount.text = item.count.toString()
            binding.root.setOnClickListener {
                listener.onClick(item)
                binding.cardDptRecycler.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
            }
            val subList = mutableListOf<DepartmentSubData>()
            item.childDepartment.forEach { child ->
                subList.add(DepartmentSubData(child, null, DptSubDataType.CHILD))
            }
            item.users.forEach { user ->
                subList.add(DepartmentSubData(null, user, DptSubDataType.USER))
            }
            val adapter = DepartmentSubListAdapter()
            binding.cardDptRecycler.adapter = adapter
            adapter.submitList(subList)
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<Department>() {
            override fun areItemsTheSame(oldItem: Department, newItem: Department): Boolean =
                oldItem.code == newItem.code


            override fun areContentsTheSame(oldItem: Department, newItem: Department): Boolean =
                oldItem == newItem

        }
    }
}