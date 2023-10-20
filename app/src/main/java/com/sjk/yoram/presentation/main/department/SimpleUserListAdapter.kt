package com.sjk.yoram.presentation.main.department

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.sjk.yoram.R
import com.sjk.yoram.data.entity.SimpleUser
import com.sjk.yoram.databinding.DptUserItemBinding
import com.sjk.yoram.presentation.common.listener.UserItemClickListener

class SimpleUserListAdapter(private val clickListener: UserItemClickListener): ListAdapter<SimpleUser, SimpleUserListAdapter.ViewHolder>(
    diffUtil
) {
    private var keyword = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: DptUserItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.dpt_user_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    inner class ViewHolder(private val binding: DptUserItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SimpleUser, listener: UserItemClickListener) {
            var avatar = if (item.avatar.isNullOrEmpty()) "http://hyuny840501.cafe24.com:8080/api/user/avatar?id=-1" else item.avatar
            binding.dptAvatarIv.load(avatar) {
                crossfade(true)
                crossfade(500)
                placeholder(R.drawable.ic_avatar)
                transformations(CircleCropTransformation())
            }

            binding.dptNameTv.text = item.name
            binding.dptNamePos.text = " ${item.position_name}"
            binding.dptSubTv.text = if (item.department_name.isNullOrEmpty()) "성도" else item.department_name
            binding.root.setOnClickListener { listener.onClick(item) }
        }
    }

    fun submitList(list: List<SimpleUser>, keyword: String) {
        super.submitList(list)
        this.keyword = keyword
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<SimpleUser>() {
            override fun areItemsTheSame(oldItem: SimpleUser, newItem: SimpleUser): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: SimpleUser, newItem: SimpleUser): Boolean = oldItem == newItem
        }
    }
}