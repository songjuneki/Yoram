package com.sjk.yoram.model.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sjk.yoram.R
import com.sjk.yoram.databinding.ListUserManagerDptBinding
import com.sjk.yoram.model.Department

class UserManagerDepartmentListAdapter(val changedListener: UserManagerDepartmentChangedListener): ListAdapter<Department, UserManagerDepartmentListAdapter.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ListUserManagerDptBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_user_manager_dpt, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ListUserManagerDptBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Department) {
            binding.item = item
            binding.selectedDpt = changedListener.selectedDpt
            binding.listUserManagerDptName.text = item.name

            binding.listUserManagerDptCheck.setOnClickListener {
                changedListener.onChanged(item)
                changedListener.selectedDpt = item
            }

            if (item.childDepartment.isNotEmpty()) {
                binding.listUserManagerDptArrow.visibility = View.VISIBLE
                binding.root.setOnClickListener {
                    item.isExpanded = !item.isExpanded
                    binding.listUserManagerDptChild.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
                    changedListener.onExpanded()
                }

                binding.listUserManagerDptChild.visibility = if (item.childDepartment.isNotEmpty() && item.isExpanded) View.VISIBLE else View.GONE
                binding.listUserManagerDptChild.adapter = UserManagerDepartmentListAdapter(changedListener).apply { this.submitList(item.childDepartment) }
            }
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<Department>() {
            override fun areContentsTheSame(oldItem: Department, newItem: Department): Boolean =
                oldItem == newItem
            override fun areItemsTheSame(oldItem: Department, newItem: Department): Boolean =
                oldItem.code == newItem.code
        }
    }
}

interface UserManagerDepartmentChangedListener {
    var selectedDpt: Department
    fun onChanged(changedDpt: Department)
    fun onExpanded()
}