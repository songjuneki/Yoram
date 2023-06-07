package com.sjk.yoram.model.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sjk.yoram.R
import com.sjk.yoram.databinding.CardDepartmentBinding
import com.sjk.yoram.model.DepartmentNode
import com.sjk.yoram.model.DepartmentNodeSubData
import com.sjk.yoram.model.DepartmentNodeSubDataType
import com.sjk.yoram.model.DepartmentSubData
import com.sjk.yoram.model.ui.listener.DepartmentItemClickListener
import com.sjk.yoram.model.ui.listener.UserItemClickListener

class DepartmentNodeListAdapter(private val permission: Int,
                                private val clickListener: DepartmentItemClickListener,
                                private val userClickListener: UserItemClickListener?): ListAdapter<DepartmentNode, DepartmentNodeListAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CardDepartmentBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.card_department, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }


    inner class ViewHolder(private val binding: CardDepartmentBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DepartmentNode, listener: DepartmentItemClickListener) {
            binding.cardDptTitle.text = item.name
            binding.cardDptCount.text = item.count.toString()
            binding.root.setOnClickListener {
                listener.onClick(item)
                binding.cardDptRecycler.visibility = if(item.isExpanded) View.VISIBLE else View.GONE
            }

            val subList = mutableListOf<DepartmentNodeSubData>()
            subList.addAll(item.child.map { DepartmentNodeSubData(it, null, DepartmentNodeSubDataType.DEPARTMENT) })
            subList.addAll(item.users.map { DepartmentNodeSubData(null, it, DepartmentNodeSubDataType.USER) })

            val subAdapter = DepartmentNodeSubListAdapter(permission, clickListener, userClickListener)
            binding.cardDptRecycler.adapter = subAdapter
            subAdapter.submitList(subList)
        }

    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<DepartmentNode>() {
            override fun areContentsTheSame(
                oldItem: DepartmentNode,
                newItem: DepartmentNode
            ): Boolean = oldItem.code == newItem.code

            override fun areItemsTheSame(
                oldItem: DepartmentNode,
                newItem: DepartmentNode
            ): Boolean = oldItem == newItem
        }
    }
}