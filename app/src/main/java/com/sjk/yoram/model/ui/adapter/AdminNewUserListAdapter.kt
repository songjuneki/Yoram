package com.sjk.yoram.model.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sjk.yoram.R
import com.sjk.yoram.databinding.ListAdminNewUserItemBinding
import com.sjk.yoram.model.dto.NewUserForAdmin
import com.sjk.yoram.model.ui.listener.AdminNewUserAcceptListener

class AdminNewUserListAdapter(private val listener: AdminNewUserAcceptListener): ListAdapter<NewUserForAdmin, AdminNewUserListAdapter.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ListAdminNewUserItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_admin_new_user_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ListAdminNewUserItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NewUserForAdmin) {
            binding.item = item

            binding.listAdminNewUserItemAccept.setOnClickListener {
                listener.onAccept(item.id, absoluteAdapterPosition)
            }
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<NewUserForAdmin>() {
            override fun areContentsTheSame(
                oldItem: NewUserForAdmin,
                newItem: NewUserForAdmin
            ): Boolean = oldItem == newItem

            override fun areItemsTheSame(
                oldItem: NewUserForAdmin,
                newItem: NewUserForAdmin
            ): Boolean = oldItem.id == newItem.id
        }
    }
}