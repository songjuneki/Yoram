package com.sjk.yoram.view.fragment.main.board

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragBoardDetailBinding
import com.sjk.yoram.model.YoramFragment
import com.sjk.yoram.model.dto.BoardMedia
import com.sjk.yoram.model.dto.BoardMediaType
import com.sjk.yoram.model.ui.view.BoardContentTextView
import com.sjk.yoram.model.ui.view.BoardMediaImageView
import com.sjk.yoram.viewmodel.FragBoardViewModel

class BoardDetailFragment: YoramFragment<FragBoardDetailBinding>(R.layout.frag_board_detail) {
    private val viewModel: FragBoardViewModel by navGraphViewModels(R.id.navi_board)


    override fun init() {
        binding.vm = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            binding.fragBoardDetailLoadingLayout.visibility = View.VISIBLE

            val contentViewList = mutableListOf<View>()

            val board = viewModel.detailBoard.value
            val content = StringBuilder(board?.board_content ?: "")
            val mediaList = board?.board_media_list?.toMutableList() ?: mutableListOf()

            val regex = Regex("\\\\!%MEDIA%!\\\\")
            var match = regex.find(content.toString())

            Log.d("JKJK", "content=${board?.board_content}")
            Log.d("JKJK", "media=$mediaList")
            if (match == null)
                contentViewList.add(makeViewFromContent(content.toString()))

            while (match != null) {
                val beforeContent = content.substring(0, match.range.first)
                content.delete(0, match.range.last+1)
                val media = mediaList.removeFirstOrNull()

                if (beforeContent.isNotBlank())
                    contentViewList.add(makeViewFromContent(beforeContent))

                if (media != null && media !is BoardMedia.Empty)
                    contentViewList.add(makeViewFromMedia(media))

                match = regex.find(content.toString())
            }

            contentViewList.forEach {
                binding.fragBoardDetailBodyContentLayout.addView(it)
            }
        }.invokeOnCompletion {
            binding.fragBoardDetailLoadingLayout.visibility = View.GONE
        }

        viewModel.backEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigateUp()
            }
        }
    }


    private fun makeViewFromContent(content: String): View {
        return BoardContentTextView(requireContext()).apply { this.text = content }
    }

    private fun makeViewFromMedia(media: BoardMedia): View {
        return when (media.type) {
            BoardMediaType.IMAGE -> {
                media as BoardMedia.Image
                Log.d("JKJK", "url=${media.url}")
                BoardMediaImageView(media.url, requireContext())
            }
            BoardMediaType.YOUTUBE -> TextView(requireContext()).apply {
                this.text = "유튜브"
            }
            BoardMediaType.LINK -> TextView(requireContext()).apply {
                this.text = "링크"
            }
            BoardMediaType.FILE -> TextView(requireContext()).apply {
                this.text = "파일"
            }
            else -> return View(requireContext())
        }
    }
}