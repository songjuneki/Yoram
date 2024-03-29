package com.sjk.yoram.presentation.main.my.preference.admin.department

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sjk.yoram.R
import com.sjk.yoram.data.entity.Department
import com.sjk.yoram.databinding.ListAdminDepartmentItemBinding
import com.sjk.yoram.presentation.common.listener.AdminDepartmentClickListener

class AdminDepartmentListAdapter(private val clickListener: AdminDepartmentClickListener, private val currentCode: Int = 0): ListAdapter<Department, AdminDepartmentListAdapter.ViewHolder>(
    diffUtil
) {
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
        return currentList.count { it.parent == currentCode }
    }

    override fun getItem(position: Int): Department {
        return currentList.filter { it.parent == currentCode }[position]
    }

    inner class ViewHolder(private val binding: ListAdminDepartmentItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Department) {
            val subAdapter = AdminDepartmentListAdapter(this@AdminDepartmentListAdapter.clickListener, item.code)
            var isExpanded = false
            var listCount = currentList.count { it.parent == item.code }
            binding.listAdminDepartmentItemRootName.text = item.name
            binding.listAdminDepartmentItemSub.adapter = subAdapter
            subAdapter.submitList(currentList)

            binding.listAdminDepartmentItemRootLayout.setOnClickListener {
                isExpanded = !isExpanded

                if (listCount < 1)
                    binding.listAdminDepartmentItemSubEmpty.visibility = if (isExpanded) View.VISIBLE else View.GONE
                else
                    binding.listAdminDepartmentItemSub.visibility = if (isExpanded) View.VISIBLE else View.GONE

                if (isExpanded)
                    binding.listAdminDepartmentItemRootArrow.setImageDrawable(binding.root.context.getDrawable(R.drawable.ic_baseline_arrow_drop_down_24))
                else
                    binding.listAdminDepartmentItemRootArrow.setImageDrawable(binding.root.context.getDrawable(R.drawable.ic_baseline_arrow_right_24))
            }


            if (listCount > 0) {
                binding.listAdminDepartmentItemSubEmpty.visibility = View.GONE
            } else {
                binding.listAdminDepartmentItemSub.visibility = View.GONE
            }

            binding.listAdminDepartmentItemRootEdit.setOnClickListener {
                this@AdminDepartmentListAdapter.clickListener.onEditClick(item.code)
            }
            binding.listAdminDepartmentItemRootAdd.setOnClickListener {
                this@AdminDepartmentListAdapter.clickListener.onAddClick(item.code)
            }
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<Department>() {
            override fun areItemsTheSame(oldItem: Department, newItem: Department): Boolean = oldItem.code == newItem.code
            override fun areContentsTheSame(oldItem: Department, newItem: Department): Boolean = oldItem == newItem
        }
    }
}