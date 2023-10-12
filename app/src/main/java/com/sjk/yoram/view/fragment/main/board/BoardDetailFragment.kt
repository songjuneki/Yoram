package com.sjk.yoram.view.fragment.main.board

import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragBoardDetailBinding
import com.sjk.yoram.model.dto.Board
import com.sjk.yoram.model.dto.BoardContentsBody
import com.sjk.yoram.model.dto.BoardMedia
import com.sjk.yoram.model.dto.BoardMediaType
import com.sjk.yoram.model.ui.view.BoardContentTextView
import com.sjk.yoram.model.ui.view.BoardMediaImageView
import com.sjk.yoram.model.ui.view.BoardMediaYoutubeView
import com.sjk.yoram.viewmodel.BoardFragmentUiState
import com.sjk.yoram.viewmodel.FragBoardViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BoardDetailFragment: Fragment() {
    private lateinit var binding: FragBoardDetailBinding
    private val viewModel: FragBoardViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_board_detail, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.vm = viewModel

        initBackPressedDispatcher()

        binding.fragBoardDetailBodyContentLayout.removeAllViews()
        binding.fragBoardDetailTopBack.setOnClickListener {
            viewModel.moveBoardDetail(null)
        }

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bindState(
            uiState = viewModel.uiState
        )
    }

    private fun FragBoardDetailBinding.bindState(
        uiState: StateFlow<BoardFragmentUiState>,
    ) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    uiState
                        .mapNotNull { it.boardDetail }
                        .distinctUntilChanged()
                        .collectLatest {
                            fragBoardDetailTopTitle.text = it.board_title
                            fragBoardDetailTopTitle.hint = it.board_title
                            fragBoardDetailTopTitle.contentDescription = it.board_title
                            fragBoardDetailTopTitle.isSingleLine = true
                            fragBoardDetailTopTitle.isSelected = true
                            fragBoardDetailBodyCategoryName.text = it.category_name
                            fragBoardDetailBodyOwnerName.text = it.owner_user_name
                            fragBoardDetailBodyOwnerDate.text = it.board_date
                            fragBoardDetailBodyLastUpdate.text = it.board_update

                            fragBoardDetailBodyOptionLayout.isVisible = it.board_option_script.isNotBlank() && it.board_option_script_date.isNotBlank()
                            val scriptDate = "${it.board_option_script_date} 설교"
                            fragBoardDetailBodyOptionDate.text = scriptDate
                            fragBoardDetailBodyOptionScriptText.text = it.board_option_script

                            parseContents(it)
                        }
                }

                launch {
                    uiState
                        .filter { !it.isBoardDetail && it.boardDetail == null }
                        .map { it.isBoardDetail }
                        .collectLatest {
                            if (!it) findNavController().navigateUp()
                        }
                }
            }
        }
    }

    private fun FragBoardDetailBinding.parseContents(board: Board) {
        val bodyQueue = board.parseContentsBody()

        for (boardContentsBody in bodyQueue) {
            when (boardContentsBody) {
                is BoardContentsBody.Contents -> fragBoardDetailBodyContentLayout.addView(makeViewFromContent(boardContentsBody.data))
                is BoardContentsBody.Media -> fragBoardDetailBodyContentLayout.addView(makeViewFromMedia(boardContentsBody.data))
            }
        }
    }

    private fun makeViewFromContent(content: String): View {
        return BoardContentTextView(requireContext(), content)
    }

    private fun makeViewFromMedia(media: BoardMedia): View {
        return when (media.type) {
            BoardMediaType.IMAGE -> {
                media as BoardMedia.Image
                BoardMediaImageView(media.url, requireContext())
            }
            BoardMediaType.YOUTUBE -> {
                media as BoardMedia.Youtube
                BoardMediaYoutubeView(media, lifecycle, requireContext())
            }
            BoardMediaType.LINK -> {
                media as BoardMedia.Link
//                BoardMediaLinkView(requireContext(), media)
                makeViewFromContent("").apply {
                    this as AppCompatTextView
                    this.linksClickable = true
                    this.autoLinkMask = Linkify.WEB_URLS
                }
            }
            BoardMediaType.FILE -> TextView(requireContext()).apply {
                this.text = "파일"
            }
            else -> return View(requireContext())
        }
    }

    override fun onDestroy() {
        binding.fragBoardDetailBodyContentLayout.children.filter {
            it is BoardMediaYoutubeView
        }.forEach {
            (it as BoardMediaYoutubeView).releaseYoutube()
        }
        super.onDestroy()
    }

    private fun initBackPressedDispatcher() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            viewModel.moveBoardDetail(null)
        }
    }
}