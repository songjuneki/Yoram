package com.sjk.yoram.presentation.main.home

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragHomeBinding
import com.sjk.yoram.presentation.main.board.BoardCategoryListAdapter
import com.sjk.yoram.presentation.main.MainViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class HomeFragment: Fragment() {
    private lateinit var binding: FragHomeBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val homeViewModel: FragHomeViewModel by navGraphViewModels(R.id.navi_home) { FragHomeViewModel.Factory(requireActivity().application) }

    private var backPressedTime = 0L

    private lateinit var bannerAdapter: HomeBannerAdapter
    private lateinit var boardCategoryAdapter: BoardCategoryListAdapter
    private lateinit var boardAdapter: HomeBoardListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_home, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.mainVM = mainViewModel
        binding.homeVM = homeViewModel

        bannerAdapter = HomeBannerAdapter()
        boardCategoryAdapter = BoardCategoryListAdapter(
            onCategoryChanged = homeViewModel::categoryChanged,
            onSelectedClick = {}
        )
        boardAdapter = HomeBoardListAdapter(
            onClickBoard = {
                mainViewModel.fragMoveBoard(it)
            },
            onClickMore = {
                mainViewModel.fragMoveBoard(
                    category = homeViewModel.uiState.value.currentBoardCategory
                )
            }
        )

        binding.bindState(
            uiState = homeViewModel.uiState,
            bannerAdapter = bannerAdapter,
            categoryAdapter = boardCategoryAdapter,
            boardAdapter = boardAdapter
        )

        initBackPressedDispatcher()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun FragHomeBinding.bindState(
        uiState: StateFlow<FragHomeUiState>,
        bannerAdapter: HomeBannerAdapter,
        categoryAdapter: BoardCategoryListAdapter,
        boardAdapter: HomeBoardListAdapter
    ) {
        bindBanner(
            uiState = uiState,
            bannerAdapter = bannerAdapter
        )
        bindBoard(
            uiState = uiState,
            categoryAdapter = categoryAdapter,
            boardAdapter = boardAdapter
        )
    }

    private fun FragHomeBinding.bindBanner(
        uiState: StateFlow<FragHomeUiState>,
        bannerAdapter: HomeBannerAdapter
    ) {
        homeBannerPager.adapter = bannerAdapter
        homeBannerIndicator.setViewPager2(homeBannerPager)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    uiState
                        .map { it.bannerList }
                        .distinctUntilChanged()
                        .collectLatest(bannerAdapter::fetchBanner)
                }
            }
        }
    }

    private fun FragHomeBinding.bindBoard(
        uiState: StateFlow<FragHomeUiState>,
        categoryAdapter: BoardCategoryListAdapter,
        boardAdapter: HomeBoardListAdapter
    ) {
        homeBoardCategoryList.adapter = categoryAdapter

        homeBoardList.adapter = boardAdapter
        homeBoardList.layoutManager = object: LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                val screenWidth = requireContext().resources.displayMetrics.widthPixels
                val screenHeight = requireContext().resources.displayMetrics.heightPixels
                lp?.width = screenWidth / 2 + screenWidth / 5
                lp?.height = screenHeight / 3
                return true
            }
        }
        homeBoardList.addItemDecoration(object: ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.right += (parent.width * 0.05).roundToInt()
            }
        })


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    uiState
                        .map { Pair(it.boardCategoryList, it.currentBoardCategory) }
                        .distinctUntilChanged()
                        .collectLatest { (list, category) ->
                            categoryAdapter.submitListWithSelection(list, category)
                        }
                }

                launch {
                    uiState
                        .map { it.boardList }
                        .distinctUntilChanged()
                        .collectLatest {
                            boardAdapter.submitList(it) { homeBoardList.scrollToPosition(0) }
                        }
                }
            }
        }

    }

    override fun onResume() {
        homeViewModel.loadNewHomeData()
        mainViewModel.loadLoginData()
        mainViewModel.loadGiveAmount()
        super.onResume()
        binding.homeBoardList.invalidateItemDecorations()
    }

    private fun initBackPressedDispatcher() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            val currentTime = System.currentTimeMillis()
            if (backPressedTime + 2000 > currentTime)
                requireActivity().finish()
            else {
                backPressedTime = currentTime
                Toast.makeText(requireContext(), "앱을 종료하려면 한번 더 눌러주세요", Toast.LENGTH_LONG).show()
            }
        }
    }
}