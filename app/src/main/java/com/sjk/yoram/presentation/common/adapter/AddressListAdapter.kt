package com.sjk.yoram.presentation.common.adapter

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sjk.yoram.R
import com.sjk.yoram.databinding.ListDialogAddressItemBinding
import com.sjk.yoram.data.entity.Juso

class AddressListAdapter(private val clickListener: AddressItemClickListener): ListAdapter<Juso, AddressListAdapter.ViewHolder>(diffUtil) {
    interface AddressItemClickListener {
        fun onClick(juso: Juso)
    }

    private var keyword: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ListDialogAddressItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_dialog_address_item,
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), keyword, clickListener)
    }

    class ViewHolder(private val binding: ListDialogAddressItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Juso, keyword: String, listener: AddressItemClickListener) {
            binding.juso = item
            binding.listener = listener

            val defaultColor = ContextCompat.getColor(binding.root.context, R.color.xd_light_title)
            val keywordColor = ContextCompat.getColor(binding.root.context, R.color.xd_light_dot_indicator_enabled)
            var startKeyword = item.roadAddr.indexOf(keyword)
            var endKeyword = startKeyword + keyword.length
            if (startKeyword == -1) {startKeyword = 0; endKeyword = 0}

            val road = SpannableString(item.roadAddr)
            road.setSpan(ForegroundColorSpan(defaultColor), 0, item.roadAddr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            road.setSpan(ForegroundColorSpan(keywordColor), startKeyword, endKeyword, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.listRoadAddressTv.text = road
            binding.listAddressTv.text = "[지번] " + item.jibunAddr
        }
    }


    fun submitList(list: List<Juso>?, keyword: String) {
        this.keyword = keyword
        this.submitList(list)
    }


    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<Juso>() {
            override fun areItemsTheSame(
                oldItem: Juso,
                newItem: Juso
            ): Boolean =
                oldItem.roadAddr == newItem.roadAddr

            override fun areContentsTheSame(
                oldItem: Juso,
                newItem: Juso
            ): Boolean =
                oldItem == newItem
        }
    }
}