package com.sjk.yoram.view.fragment.main.board

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.paging.PagingData
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragBoardBinding
import com.sjk.yoram.model.dto.Board
import com.sjk.yoram.model.ui.adapter.BoardCategoryListAdapter
import com.sjk.yoram.model.ui.adapter.BoardListAdapter
import com.sjk.yoram.viewmodel.BoardFragmentUiAction
import com.sjk.yoram.viewmodel.BoardFragmentUiState
import com.sjk.yoram.viewmodel.FragBoardViewModel
import com.sjk.yoram.viewmodel.MainViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class BoardFragment: Fragment() {
    private lateinit var binding: FragBoardBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: FragBoardViewModel by activityViewModels { FragBoardViewModel.Factory(requireActivity().application, owner = requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_board, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.mainVM = mainViewModel
        binding.vm = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bindState(
            uiState = viewModel.uiState,
            boardPagingData = viewModel.boardPagingDataFlow,
            uiAction = viewModel.accept
        )

    }

    private fun FragBoardBinding.bindState(
        uiState: StateFlow<BoardFragmentUiState>,
        boardPagingData: Flow<PagingData<Board>>,
        uiAction: (BoardFragmentUiAction) -> Unit
    ) {
        val categoryAdapter = BoardCategoryListAdapter(
            onCategoryChanged = uiAction,
            onSelectedClick = { fragBoardBodyRecycler.smoothScrollToPosition(0) }
        )
        val boardAdapter = BoardListAdapter(
            onBoardClick = uiAction
        )

        bindCategoryList(
            uiState = uiState,
            categoryAdapter = categoryAdapter,
            onCategoryChanged = uiAction
        )

        bindBoardList(
            uiState = uiState,
            categoryListAdapter = categoryAdapter,
            boardListAdapter = boardAdapter,
            boardPagingData = boardPagingData,
            onBoardDetail = uiAction
        )
    }

    private fun FragBoardBinding.bindCategoryList(
        uiState: StateFlow<BoardFragmentUiState>,
        categoryAdapter: BoardCategoryListAdapter,
        onCategoryChanged: (BoardFragmentUiAction.CategoryChange) -> Unit
    ) {
        fragBoardCategoryRecycler.adapter = categoryAdapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    uiState
                        .map { Pair(it.boardCatList, it.currentCategory) }
                        .collectLatest { (list, category) ->
                            Log.d("JKJK", "UI::LoadedCategoryList And submit | $list | selected=$category")
                            categoryAdapter.submitListWithSelection(list, category)
                        }
                }


                launch {
                    uiState
                        .map { it.isCatInit }
                        .collect {
                            fragBoardCategoryRecycler.isVisible = it
                            fragBoardCategoryLayoutShimmer.isVisible = !it
                        }
                }
            }
        }
    }

    private fun FragBoardBinding.bindBoardList(
        uiState: StateFlow<BoardFragmentUiState>,
        categoryListAdapter: BoardCategoryListAdapter,
        boardListAdapter: BoardListAdapter,
        boardPagingData: Flow<PagingData<Board>>,
        onBoardDetail: (BoardFragmentUiAction.OnClickBoardDetail) -> Unit
    ) {
        fragBoardBodyRecycler.adapter = boardListAdapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val absoluteCurrentCategory = categoryListAdapter.currentList.getOrNull(categoryListAdapter.currentCategoryPos)

                val isChangedCategory = uiState
                    .map { it.currentCategory != absoluteCurrentCategory }
                    .distinctUntilChanged()

                val isAlreadyBoardInit = uiState
                    .map { it.isBoardInit }
                    .distinctUntilChanged()

                val categoryChangeDetect = combine(
                    isChangedCategory,
                    isAlreadyBoardInit,
                    Boolean::and
                )
                    .distinctUntilChanged()


                launch {
                    boardPagingData
                        .collectLatest(boardListAdapter::submitData)
                }

                launch {
                    categoryChangeDetect
                        .filter { it }
                        .collectLatest {
                            fragBoardBodyRecycler.scrollToPosition(0)
                        }
                }

                launch {
                    uiState
                        .map { it.isBoardInit }
                        .distinctUntilChanged()
                        .collect {
                            fragBoardBodyRecycler.isVisible = it
                            fragBoardBodyShimmerLayout.isVisible = !it
                        }
                }

                val isClickedBoard = uiState
                    .filter { it.isBoardDetail && it.boardDetail != null }
                    .map { it.isBoardDetail }
                    .distinctUntilChanged()

                launch {
                    isClickedBoard
                        .collectLatest {
                            if (it && findNavController().currentDestination?.id == R.id.boardFragment)
                                findNavController().navigate(R.id.action_boardFragment_to_boardDetailFragment)
                        }

                }
            }
        }
    }
}