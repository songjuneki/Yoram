package com.sjk.yoram.model.ui.adapter

import android.util.Log
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
import com.sjk.yoram.model.Department
import com.sjk.yoram.model.DepartmentSubData
import com.sjk.yoram.model.DptSubDataType
import com.sjk.yoram.model.dto.SimpleUser
import com.sjk.yoram.model.ui.listener.DepartmentItemClickListener
import com.sjk.yoram.model.ui.listener.UserItemClickListener
import java.lang.IllegalArgumentException
import kotlin.math.ceil
import kotlin.math.roundToInt

class DepartmentSubListAdapter(private val permission: Int, private val clickListener: DepartmentItemClickListener, private val userClickListener: UserItemClickListener?): ListAdapter<DepartmentSubData, RecyclerView.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DptSubDataType.CHILD.ordinal -> {
                val binding: CardDepartmentBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.card_department, parent, false)
                SubViewHolder(binding)
            }
            DptSubDataType.USER.ordinal -> {
                val binding: DptUserItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.dpt_user_item, parent, false)
                UserViewHolder(binding)
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == DptSubDataType.CHILD.ordinal)
            (holder as SubViewHolder).bind(getItem(position).childDepartment!!)
        else
            (holder as UserViewHolder).bind(getItem(position).user!!)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type.ordinal
    }
    override fun getItemCount(): Int = currentList.size
    override fun getItem(position: Int): DepartmentSubData = currentList[position]

    inner class SubViewHolder(private val binding: CardDepartmentBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Department) {
            binding.cardDptTitle.text = item.name
            binding.cardDptCount.text = item.count.toString()
            binding.root.setOnClickListener {
                clickListener.onClick(item)
                binding.cardDptRecycler.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
            }
            val subList = mutableListOf<DepartmentSubData>()
            item.childDepartment.forEach { child ->
                subList.add(DepartmentSubData(child, null, DptSubDataType.CHILD))
            }
            item.users.forEach { user ->
                subList.add(DepartmentSubData(null, user, DptSubDataType.USER))
            }
            val adapter = DepartmentSubListAdapter(permission, clickListener, userClickListener)
            adapter.submitList(subList)
            binding.cardDptRecycler.adapter = adapter
        }
    }
    inner class UserViewHolder(private val binding: DptUserItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SimpleUser) {
            Log.d("JKJK", "permission adapter = $permission")
            var avatar = if (item.avatar.isNullOrEmpty()) "http://3.39.51.49:8080/api/user/avatar?id=-1" else item.avatar
            if (permission < 1) avatar = "http://3.39.51.49:8080/api/user/avatar?id=-1"
            binding.dptAvatarIv.load(avatar) {
                crossfade(true)
                placeholder(R.drawable.ic_avatar)
                transformations(CircleCropTransformation())
            }

            var name = ""
            val f = ceil(item.name.length.div(3.0)).roundToInt()
            if (permission < 1) {
                name = "${item.name.substring(0, f)}"
                for (i in f until item.name.length) name += "Ｏ"
            }else name = "${item.name}"

            binding.dptNameTv.text = "$name ${item.position_name}"
            val dptname = if (item.department_name.isNullOrEmpty()) "성도" else item.department_name
            binding.dptSubTv.text = dptname
            binding.root.setOnClickListener { userClickListener?.onClick(item) }
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<DepartmentSubData>() {
            override fun areItemsTheSame(oldItem: DepartmentSubData, newItem: DepartmentSubData): Boolean {
                if (oldItem.type != newItem.type)
                    return false

                return if (oldItem.childDepartment?.code != newItem.childDepartment?.code)
                    false
                else oldItem.user?.id == newItem.user?.id
            }

            override fun areContentsTheSame(oldItem: DepartmentSubData, newItem: DepartmentSubData): Boolean {
                return oldItem == newItem
            }

        }
    }
}