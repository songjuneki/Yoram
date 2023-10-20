package com.sjk.yoram.presentation.main.board

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sjk.yoram.R
import com.sjk.yoram.data.entity.Board
import com.sjk.yoram.data.entity.BoardMedia
import com.sjk.yoram.data.entity.BoardMediaType
import com.sjk.yoram.databinding.BoardItemBinding
import com.sjk.yoram.presentation.main.board.detail.BoardMediaListAdapter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BoardListAdapter(val onBoardClick: (Board?) -> Unit): PagingDataAdapter<Board, RecyclerView.ViewHolder>(
    diffUtil
) {
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

            val body = "${item?.board_title}\n\n${contentMediaParse(item)}".trimIndent()
            binding.boardItemBody.text = body
            binding.boardItemOwnerDate.text = item?.board_date

            if (item?.board_option_script?.isNotBlank() == true) {
                binding.boardItemScript.visibility = View.VISIBLE
                binding.boardItemScript.text = item.board_option_script
            } else {
                binding.boardItemScript.visibility = View.GONE
            }

            if (item?.board_option_script_date?.isNotBlank() == true) {
                binding.boardItemScriptDate.visibility = View.VISIBLE
                val scriptDate = "${item.board_option_script_date} 설교"
                binding.boardItemScriptDate.text = scriptDate
            } else {
                binding.boardItemScriptDate.visibility = View.GONE
            }

            // LINK 타입 작업중
            val filteredMediaList = mediaList.filter { it.type == BoardMediaType.IMAGE || it.type == BoardMediaType.YOUTUBE }
            binding.boardItemBodyMediaPager.adapter = BoardMediaListAdapter().apply {
                submitList(filteredMediaList)
            }
            binding.boardItemBodyMediaPager.updateLayoutParams {
                this.height = binding.root.context.resources.displayMetrics.heightPixels / 4
            }
            binding.boardItemBodyMediaPager.isVisible = filteredMediaList.isNotEmpty()

            binding.root.setOnClickListener {
                onBoardClick(item ?: return@setOnClickListener)
            }

        }

        private fun contentMediaParse(item: Board?): String {
            if (item == null) return ""
            val content = StringBuilder(item.board_content)

            val regex = "\\\\!%MEDIA%!\\\\".toRegex()
            var match = regex.find(content.toString())
            var index = 0

            while (match != null) {
                content.delete(match.range.first, match.range.last+1)

                val media = mediaList.getOrNull(index)
                when (media?.type) {
                    BoardMediaType.NONE -> {}
                    BoardMediaType.LINK -> {
                        media as BoardMedia.Link
                        if (media.url.endsWith("jpg", ignoreCase = true)
                            || media.url.endsWith("jpeg", ignoreCase = true)
                            || media.url.endsWith("png", ignoreCase = true)
                            || media.url.endsWith("bmp", ignoreCase = true)) {
                            mediaList.remove(media)
                            index--
                        }
                    }
                    BoardMediaType.IMAGE -> {}
                    BoardMediaType.FILE -> {}
                    BoardMediaType.YOUTUBE -> {}
                    else -> {}
                }
                index++
                match = regex.find(content.toString())
            }

            val blank = "&nbsp;".toRegex()
            match = blank.find(content.toString())
            while (match != null) {
                content.replace(match.range.first, match.range.last+1, " ")
                match = blank.find(content.toString())
            }

            return content.toString()
        }
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