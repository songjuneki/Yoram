package com.sjk.yoram.view.fragment.main.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragBoardBinding
import com.sjk.yoram.model.dto.Board
import com.sjk.yoram.model.ui.adapter.BoardCategoryListAdapter
import com.sjk.yoram.model.ui.adapter.BoardListAdapter
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

        binding.bindState(
            viewModel = viewModel,
            uiState = viewModel.uiState
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun FragBoardBinding.bindState(
        viewModel: FragBoardViewModel,
        uiState: StateFlow<BoardFragmentUiState>
    ) {
        val categoryAdapter = BoardCategoryListAdapter(
            onCategoryChanged = viewModel::changeCurrentCategory,
            onSelectedClick = { fragBoardBodyRecycler.smoothScrollToPosition(0) }
        )
        val boardAdapter = BoardListAdapter(
            onBoardClick = viewModel::moveBoardDetail
        )

        bindCategoryList(
            uiState = uiState,
            categoryAdapter = categoryAdapter,
        )

        bindBoardList(
            uiState = uiState,
            categoryListAdapter = categoryAdapter,
            boardListAdapter = boardAdapter,
            boardPagingData = viewModel.boardPagingData,
            onRefresh = viewModel::refreshBoardData
        )
    }

    private fun FragBoardBinding.bindCategoryList(
        uiState: StateFlow<BoardFragmentUiState>,
        categoryAdapter: BoardCategoryListAdapter,
    ) {
        fragBoardCategoryRecycler.adapter = categoryAdapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    uiState
                        .map { Pair(it.boardCatList, it.currentCategory) }
                        .distinctUntilChanged()
                        .collectLatest { (list, selected) ->
                            categoryAdapter.submitListWithSelection(list, selected)
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun FragBoardBinding.bindBoardList(
        uiState: StateFlow<BoardFragmentUiState>,
        categoryListAdapter: BoardCategoryListAdapter,
        boardListAdapter: BoardListAdapter,
        boardPagingData: Flow<PagingData<Board>>,
        onRefresh: () -> Job
    ) {
        fragBoardBodyRecycler.adapter = boardListAdapter

        fragBoardBodyRefreshLayout.setOnRefreshListener {
            onRefresh()
                .invokeOnCompletion {
                    fragBoardBodyRefreshLayout.isRefreshing = false
                    boardListAdapter.refresh()
                }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                val isChangedCategory = uiState
                    .map { it.currentCategory }
                    .mapLatest { it != categoryListAdapter.currentList.getOrNull(categoryListAdapter.currentCategoryPos) }

                val isPagingDataRefresh = boardListAdapter
                    .loadStateFlow
                    .distinctUntilChanged()
                    .map { it.refresh is LoadState.Loading }
                    .filter { it }

                launch {
                    boardPagingData
                        .distinctUntilChanged()
                        .collectLatest(boardListAdapter::submitData)
                }

                launch {
                    isChangedCategory
                        .filter { it }
                        .collectLatest { boardListAdapter.refresh() }
                }

                launch {
                    isPagingDataRefresh
                        .collectLatest { fragBoardBodyRecycler.scrollToPosition(0) }
                }

                launch {
                    uiState
                        .map { it.isBoardInit }
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