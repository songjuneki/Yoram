package com.sjk.yoram.model.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sjk.yoram.R
import com.sjk.yoram.databinding.BoardItemEndBinding
import com.sjk.yoram.databinding.BoardItemErrorBinding
import com.sjk.yoram.databinding.BoardItemShimmerBinding

class BoardListLoadStateAdapter: LoadStateAdapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): RecyclerView.ViewHolder {
        if (loadState.endOfPaginationReached)
            return BoardOfEndViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.board_item_end, parent, false))

        return when (loadState) {
            is LoadState.Error -> BoardOfErrorViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.board_item_end, parent, false))
            is LoadState.Loading -> BoardOfShimmerViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.board_item_shimmer, parent, false))
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, loadState: LoadState) {
        if (loadState.endOfPaginationReached)
            (holder as BoardOfEndViewHolder).bind()
        when(loadState) {
            is LoadState.Error -> (holder as BoardOfErrorViewHolder).bind()
            is LoadState.Loading -> (holder as BoardOfShimmerViewHolder).bind()
        }
    }

    private inner class BoardOfShimmerViewHolder(val binding: BoardItemShimmerBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            Log.d("JKJK", "BoardOfLoadingViewHolder")
        }
    }

    private inner class BoardOfEndViewHolder(val binding: BoardItemEndBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            Log.d("JKJK", "BoardOfEndViewHolder")
        }
    }


    private inner class BoardOfErrorViewHolder(val binding: BoardItemErrorBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            Log.d("JKJK", "BoardOfErrorViewHolder")
        }
    }
}