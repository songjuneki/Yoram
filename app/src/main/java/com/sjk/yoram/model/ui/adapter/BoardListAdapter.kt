package com.sjk.yoram.model.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sjk.yoram.R
import com.sjk.yoram.databinding.BoardItemBinding
import com.sjk.yoram.model.dto.Board
import com.sjk.yoram.model.dto.BoardMedia
import com.sjk.yoram.model.ui.listener.BoardClickListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BoardListAdapter(val boardClickListener: BoardClickListener): PagingDataAdapter<Board, RecyclerView.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BoardViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.board_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BoardViewHolder).bind(getItem(position), position)
    }


    private inner class BoardViewHolder(val binding: BoardItemBinding): RecyclerView.ViewHolder(binding.root) {
        var mediaList = mutableListOf<BoardMedia>()
        fun bind(item: Board?, pos: Int) {
            mediaList = item?.board_media_list?.toMutableList() ?: mutableListOf()

            binding.boardItemCategoryName.text = item?.category_name
            binding.boardItemOwnerName.text = item?.owner_user_name
            binding.boardItemBody.text = item?.board_content
            binding.boardItemOwnerDate.text = item?.board_date


            binding.root.setOnClickListener {
                boardClickListener.onClick(item!!)
            }


        }

//        private fun contentMediaParse(): String {
//            mediaList.forEach {
//
//            }
//        }
    }


    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<Board>() {
            override fun areContentsTheSame(oldItem: Board, newItem: Board): Boolean {
                val oldDate = LocalDateTime.parse(oldItem.board_update, DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm"))
                val newDate = LocalDateTime.parse(newItem.board_update, DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm"))
                return oldDate.isEqual(newDate)
            }

            override fun areItemsTheSame(oldItem: Board, newItem: Board): Boolean {
                return oldItem.board_id == newItem.board_id
            }
        }
    }
}