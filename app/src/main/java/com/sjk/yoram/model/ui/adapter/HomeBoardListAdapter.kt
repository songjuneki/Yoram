package com.sjk.yoram.model.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sjk.yoram.R
import com.sjk.yoram.databinding.ListHomeBoardItemBinding
import com.sjk.yoram.databinding.ListHomeBoardMoreItemBinding
import com.sjk.yoram.model.dto.Board
import com.sjk.yoram.model.dto.BoardMedia
import com.sjk.yoram.model.dto.BoardMediaType
import jp.wasabeef.transformers.coil.GrayscaleTransformation
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeBoardListAdapter(private val onClickBoard: (Board) -> Unit,
                           private val onClickMore: () -> Unit): ListAdapter<Board, RecyclerView.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == MORE_BUTTON_TYPE)
            HomeBoardListMoreButtonViewHolder(
                DataBindingUtil.inflate(inflater, R.layout.list_home_board_more_item, parent, false))
        else
            HomeBoardListItemViewHolder(
                DataBindingUtil.inflate(inflater, R.layout.list_home_board_item, parent, false)
            )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == MORE_BUTTON_TYPE)
            return
        else
            (holder as HomeBoardListItemViewHolder).bind(getItem(position), position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).board_id == MORE_BUTTON_TYPE)
            MORE_BUTTON_TYPE
        else
            super.getItemViewType(position)
    }

    inner class HomeBoardListItemViewHolder(val binding: ListHomeBoardItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Board, pos: Int) {
            if (item.board_media_list.isEmpty()) {
                binding.listHomeBoardItemImg.load(R.drawable.ic_icon) {
                    transformations(GrayscaleTransformation())
                }
                binding.listHomeBoardItemImg.scaleType = ImageView.ScaleType.CENTER_INSIDE
                binding.listHomeBoardItemImg.scaleX = 0.5f
                binding.listHomeBoardItemImg.scaleY = 0.5f
            } else {
                item.board_media_list.find {
                    it.type == BoardMediaType.IMAGE || it.type == BoardMediaType.YOUTUBE
                }?.let {
                    binding.listHomeBoardItemImg.scaleType = ImageView.ScaleType.CENTER_CROP
                    binding.listHomeBoardItemImg.scaleX = 1f
                    binding.listHomeBoardItemImg.scaleY = 1f
                    when (it.type) {
                        BoardMediaType.IMAGE -> {
                            binding.listHomeBoardItemImg.load((it as BoardMedia.Image).url)
                        }
                        BoardMediaType.YOUTUBE -> {
                            binding.listHomeBoardItemImg.load((it as BoardMedia.Youtube).thumbnail)
                        }
                        else -> return@let
                    }
                } ?: {
                    binding.listHomeBoardItemImg.load(R.drawable.ic_icon) {
                        transformations(GrayscaleTransformation())
                    }
                    binding.listHomeBoardItemImg.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    binding.listHomeBoardItemImg.scaleX = 0.5f
                    binding.listHomeBoardItemImg.scaleY = 0.5f
                }
            }

            binding.listHomeBoardItemTitle.text = item.board_title

            val content = item.board_content.take(128)
            if (content.contains("\\!%MEDIA%!\\"))
                binding.listHomeBoardItemContent.text = "  "
            else
                binding.listHomeBoardItemContent.text = content

            val boardDate = LocalDateTime.parse(item.board_date, DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm"))
            binding.listHomeBoardItemDate.text = boardDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")).toString()

            binding.root.setOnClickListener {
                onClickBoard(item)
            }
        }
    }

    inner class HomeBoardListMoreButtonViewHolder(val binding: ListHomeBoardMoreItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onClickMore.invoke()
            }
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<Board>() {
            override fun areContentsTheSame(oldItem: Board, newItem: Board): Boolean = oldItem == newItem
            override fun areItemsTheSame(oldItem: Board, newItem: Board): Boolean =
                oldItem.category_id == newItem.category_id
                        && oldItem.board_id == newItem.board_id
        }

        private const val MORE_BUTTON_TYPE = -15
    }
}