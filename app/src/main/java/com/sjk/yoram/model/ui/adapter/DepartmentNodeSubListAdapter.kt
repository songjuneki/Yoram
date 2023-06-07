package com.sjk.yoram.model.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.sjk.yoram.R
import com.sjk.yoram.databinding.CardDepartmentBinding
import com.sjk.yoram.databinding.DptUserItemBinding
import com.sjk.yoram.model.*
import com.sjk.yoram.model.dto.SimpleUser
import com.sjk.yoram.model.ui.listener.DepartmentItemClickListener
import com.sjk.yoram.model.ui.listener.UserItemClickListener

class DepartmentNodeSubListAdapter(private val permission: Int,
                                   private val clickListener: DepartmentItemClickListener,
                                   private val userClickListener: UserItemClickListener?): ListAdapter<DepartmentNodeSubData, RecyclerView.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            DepartmentNodeSubDataType.DEPARTMENT.ordinal -> {
                val binding: CardDepartmentBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.card_department, parent, false)
                DepartmentViewHolder(binding)
            }
            DepartmentNodeSubDataType.USER.ordinal -> {
                val binding: DptUserItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.dpt_user_item, parent, false)
                UserViewHolder(binding)
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == DepartmentNodeSubDataType.DEPARTMENT.ordinal)
            (holder as DepartmentViewHolder).bind(getItem(position).childDepartmentNode!!)
        else
            (holder as UserViewHolder).bind(getItem(position).user!!)
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type.ordinal
    override fun getItemCount(): Int = currentList.size
    override fun getItem(position: Int): DepartmentNodeSubData = currentList[position]

    inner class DepartmentViewHolder(private val binding: CardDepartmentBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DepartmentNode) {
            binding.cardDptTitle.text = item.name
            binding.cardDptCount.text = item.count.toString()
            binding.root.setOnClickListener {
                clickListener.onClick(item)
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

    inner class UserViewHolder(private val binding: DptUserItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SimpleUser) {
            var avatar = if(item.avatar.isEmpty()) "http://hyuny840501.cafe24.com:8080/api/user/avatar?id=-1" else item.avatar
            if (permission < 1) avatar = "http://hyuny840501.cafe24.com:8080/api/user/avatar?id=-1"
            binding.dptAvatarIv.load(avatar) {
                crossfade(true)
                crossfade(500)
                placeholder(R.drawable.ic_avatar)
                transformations(CircleCropTransformation())
            }

            val name = item.name.replace('*', 'Ｏ')

            binding.dptNameTv.text = name
            binding.dptNamePos.text = item.position_name
            if(item.department_name.isEmpty())
                binding.dptSubTv.text = "성도"
            else
                binding.dptSubTv.text = item.department_name

            binding.root.setOnClickListener {
                userClickListener?.onClick(item)
            }
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<DepartmentNodeSubData>() {
            override fun areItemsTheSame(
                oldItem: DepartmentNodeSubData,
                newItem: DepartmentNodeSubData
            ): Boolean {
                if(oldItem.type != newItem.type)
                    return false

                return when(newItem.type) {
                    DepartmentNodeSubDataType.DEPARTMENT -> oldItem.childDepartmentNode?.code == newItem.childDepartmentNode?.code
                    DepartmentNodeSubDataType.USER -> oldItem.user?.id == newItem.user?.id
                }
            }

            override fun areContentsTheSame(
                oldItem: DepartmentNodeSubData,
                newItem: DepartmentNodeSubData
            ): Boolean = oldItem == newItem
        }
    }
}