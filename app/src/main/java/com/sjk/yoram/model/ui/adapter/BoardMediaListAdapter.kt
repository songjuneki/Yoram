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
import coil.size.Scale
import coil.size.ViewSizeResolver
import com.sjk.yoram.R
import com.sjk.yoram.databinding.BoardMediaItemFileBinding
import com.sjk.yoram.databinding.BoardMediaItemImageBinding
import com.sjk.yoram.databinding.BoardMediaItemLinkBinding
import com.sjk.yoram.databinding.BoardMediaItemNoneBinding
import com.sjk.yoram.databinding.BoardMediaItemYoutubeBinding
import com.sjk.yoram.model.dto.BoardMedia
import com.sjk.yoram.model.dto.BoardMediaType
import com.soulsurfer.android.PageInfo
import com.soulsurfer.android.PageInfoListener
import com.soulsurfer.android.SoulSurfer
import jp.wasabeef.transformers.coil.BlurTransformation

class BoardMediaListAdapter(): ListAdapter<BoardMedia, RecyclerView.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            BoardMediaType.NONE.ordinal -> NoneBoardMediaViewHolder(DataBindingUtil.inflate(inflater, R.layout.board_media_item_none, parent, false))
            BoardMediaType.LINK.ordinal -> LinkBoardMediaViewHolder(DataBindingUtil.inflate(inflater, R.layout.board_media_item_link, parent, false))
            BoardMediaType.IMAGE.ordinal -> ImageBoardMediaViewHolder(DataBindingUtil.inflate(inflater, R.layout.board_media_item_image, parent, false))
            BoardMediaType.YOUTUBE.ordinal -> YoutubeBoardMediaViewHolder(DataBindingUtil.inflate(inflater, R.layout.board_media_item_youtube, parent, false))
            BoardMediaType.FILE.ordinal -> FileBoardMediaViewHolder(DataBindingUtil.inflate(inflater, R.layout.board_media_item_file, parent, false))
            else -> { throw IllegalArgumentException() }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            BoardMediaType.NONE.ordinal -> {}
            BoardMediaType.LINK.ordinal -> (holder as LinkBoardMediaViewHolder).bind(getItem(position) as BoardMedia.Link)
            BoardMediaType.IMAGE.ordinal -> (holder as ImageBoardMediaViewHolder).bind(getItem(position) as BoardMedia.Image)
            BoardMediaType.YOUTUBE.ordinal -> (holder as YoutubeBoardMediaViewHolder).bind(getItem(position) as BoardMedia.Youtube)
            BoardMediaType.FILE.ordinal -> (holder as FileBoardMediaViewHolder).bind(getItem(position) as BoardMedia.File)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type.ordinal
    }

    private inner class NoneBoardMediaViewHolder(val binding: BoardMediaItemNoneBinding): RecyclerView.ViewHolder(binding.root) {

    }

    private inner class ImageBoardMediaViewHolder(val binding: BoardMediaItemImageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BoardMedia.Image) {
            binding.boardMediaItemImageImage.load(item.url) {
                allowHardware(true)
                size(ViewSizeResolver(binding.boardMediaItemImageImage))
                scale(Scale.FILL)
                crossfade(true)
                crossfade(500)
                listener(onStart = { binding.boardMediaItemImageProgress.visibility = View.VISIBLE },
                    onSuccess = { _, _ ->
                        binding.boardMediaItemImageProgress.visibility = View.GONE
                    })
            }
        }
    }


    private inner class LinkBoardMediaViewHolder(val binding: BoardMediaItemLinkBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BoardMedia.Link) {
            binding.boardMediaItemLinkProgress.visibility = View.VISIBLE
            binding.boardMediaItemLinkUrl.text = item.url
            SoulSurfer.get(item.url)
                .load(object: PageInfoListener {
                    override fun onError(url: String?) {
                        binding.boardMediaItemLinkProgress.visibility = View.GONE
                        binding.boardMediaItemLinkError.visibility = View.VISIBLE
                    }

                    override fun onPageInfoLoaded(pageInfo: PageInfo?) {
                        binding.boardMediaItemLinkTitle.text = pageInfo?.title ?: item.url
                        binding.boardMediaItemLinkDescription.text = pageInfo?.description ?: item.url
                        binding.boardMediaItemLinkThumbnail.load(pageInfo?.imageUrl) {
                            allowHardware(true)
                            size(ViewSizeResolver(binding.boardMediaItemLinkThumbnail))
                            scale(Scale.FILL)
                            crossfade(true)
                            crossfade(500)
                            listener(onSuccess = { _, _ ->
                                binding.boardMediaItemLinkError.visibility = View.GONE
                            }, onError = { _, _ ->
                                binding.boardMediaItemLinkError.visibility = View.VISIBLE
                            })
                        }
                        binding.boardMediaItemLinkProgress.visibility = View.GONE
                    }
                })
        }
    }

    private inner class YoutubeBoardMediaViewHolder(val binding: BoardMediaItemYoutubeBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BoardMedia.Youtube) {
            binding.boardMediaItemYoutubeThumbnail.load(item.thumbnail) {
                allowHardware(true)
                size(ViewSizeResolver(binding.boardMediaItemYoutubeThumbnail))
                scale(Scale.FILL)
                crossfade(true)
                crossfade(500)
                listener(onStart = {
                    binding.boardMediaItemYoutubeProgress.visibility = View.VISIBLE
                    binding.boardMediaItemYoutubeIcon.visibility = View.INVISIBLE
                },
                    onSuccess = { _, _ ->
                        binding.boardMediaItemYoutubeProgress.visibility = View.GONE
                        binding.boardMediaItemYoutubeIcon.visibility = View.VISIBLE
                    }
                )
                transformations(BlurTransformation(binding.root.context, 4, 1))
            }
        }
    }

    private inner class FileBoardMediaViewHolder(val binding: BoardMediaItemFileBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BoardMedia.File) {
            binding.boardMediaItemFileName.text = item.url
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<BoardMedia>() {
            override fun areItemsTheSame(oldItem: BoardMedia, newItem: BoardMedia): Boolean {
                if (oldItem.type == BoardMediaType.NONE && newItem.type == BoardMediaType.NONE)
                    return false
                return oldItem.type == newItem.type
            }

            override fun areContentsTheSame(oldItem: BoardMedia, newItem: BoardMedia): Boolean {
                return when (oldItem.type) {
                    BoardMediaType.LINK -> {
                        oldItem as BoardMedia.Link; newItem as BoardMedia.Link
                        oldItem.url == newItem.url
                    }
                    BoardMediaType.IMAGE -> {
                        oldItem as BoardMedia.Image; newItem as BoardMedia.Image
                        oldItem.url == newItem.url
                    }
                    BoardMediaType.FILE -> {
                        oldItem as BoardMedia.File; newItem as BoardMedia.File
                        oldItem.url == newItem.url
                    }
                    BoardMediaType.YOUTUBE -> {
                        oldItem as BoardMedia.Youtube; newItem as BoardMedia.Youtube
                        oldItem.id == newItem.id
                    }
                    else -> false
                }
            }
        }
    }
}