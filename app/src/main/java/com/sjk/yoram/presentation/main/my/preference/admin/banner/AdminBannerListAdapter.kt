package com.sjk.yoram.presentation.main.my.preference.admin.banner

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sjk.yoram.R
import com.sjk.yoram.data.entity.Banner
import com.sjk.yoram.databinding.ListAdminBannerItemBinding

class AdminBannerListAdapter(private val clickListener: AdminBannerClickListener): ListAdapter<Banner, AdminBannerListAdapter.ViewHolder>(diffUtil),
    AdminBannerItemTouchHelperListener {
    interface AdminBannerClickListener {
        fun onClick(item: Banner)
    }

    private lateinit var dragListener: ItemDragListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ListAdminBannerItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_admin_banner_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }


    @SuppressLint("ClickableViewAccessibility")
    inner class ViewHolder(private val binding: ListAdminBannerItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.listAdminBannerDrag.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) dragListener.onStartDrag(this)
                false
            }
        }
        fun bind(item: Banner, pos: Int) {
            binding.banner = item
            binding.listAdminBannerDelete.setOnClickListener {
                onRightClick(pos, this)
            }
            binding.listAdminBannerCard.setOnClickListener {
                clickListener.onClick(item)
            }
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<Banner>() {
            override fun areItemsTheSame(oldItem: Banner, newItem: Banner): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Banner, newItem: Banner): Boolean = oldItem == newItem
        }
    }

    interface ItemDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
        fun onEndDrag(currentList: List<Banner>)
    }

    fun setDragListener(listener: ItemDragListener) {
        this.dragListener = listener
    }

    fun deleteItem(item: Banner) {
        for (i in 0 until currentList.size) {
            if (currentList[i].id == item.id) {
                val list = currentList.toMutableList()
                list.removeAt(i)
                submitList(list)
                notifyDataSetChanged()
                return
            }
        }
    }

    override fun onItemMove(from_position: Int, to_position: Int): Boolean {
        val item = getItem(from_position)
        val list = currentList.toMutableList()

        list.removeAt(from_position)
        list.add(to_position, item)

        list.forEachIndexed { index, _ ->
            list[index].order = index+1
        }

        submitList(list)
        dragListener.onEndDrag(list)
        return true
    }

    override fun onItemSwipe(position: Int) {
    }

    override fun onLeftClick(position: Int, viewHolder: RecyclerView.ViewHolder?) {
        val list = currentList.toMutableList()

        list[position].show = !list[position].show

        submitList(list)
    }

    override fun onRightClick(position: Int, viewHolder: RecyclerView.ViewHolder?) {
        val list = currentList.toMutableList()

        list.removeAt(position)

        submitList(list)
    }
}