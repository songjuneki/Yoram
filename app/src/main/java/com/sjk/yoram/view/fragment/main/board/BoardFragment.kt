package com.sjk.yoram.view.fragment.main.board

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragBoardBinding
import com.sjk.yoram.model.ApiState
import com.sjk.yoram.model.YoramFragment
import com.sjk.yoram.viewmodel.FragBoardViewModel
import com.sjk.yoram.viewmodel.MainViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update

class BoardFragment: YoramFragment<FragBoardBinding>(R.layout.frag_board) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: FragBoardViewModel by navGraphViewModels(R.id.navi_board) { FragBoardViewModel.Factory(requireActivity().application) }

    private var showCategoryShimmer: Job? = null
    private var showBoardShimmer: Job? = null

    override fun init() {
        binding.mainVM = mainViewModel
        binding.vm = viewModel

        showCategoryShimmer = makeCategoryShimmerJob()
        showBoardShimmer = makeBoardShimmerJob()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.categoryList.collectLatest {  catListData ->
                when (catListData) {
                    is ApiState.Loading -> showCategoryShimmer = makeCategoryShimmerJob()
                    is ApiState.Success -> {
                        viewModel.categoryAdapter.submitList(catListData.data)
                        viewModel.currentBoardCategory.update { catListData.data?.firstOrNull() }
                        showCategory()
                    }
                    is ApiState.Error -> {
                        // 에러뷰 핸들링
                        showCategoryShimmer = makeCategoryShimmerJob()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.currentBoardCategory.collectLatest { currentCat ->
                if (currentCat == null)
                    showBoardShimmer = makeBoardShimmerJob()
                else {
                    showBoard()
                    viewModel.getBoardData(currentCat).collectLatest { data ->
                        viewModel.boardAdapter.submitData(data)
                    }
                }
            }
        }

        viewModel.moveTopOfBoardListEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                binding.fragBoardBodyRecycler.smoothScrollToPosition(0)
            }
        }

        viewModel.moveDetailEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                val bundle = Bundle().apply {
                    this.putString("content", it.board_content)
                }
                findNavController().navigate(R.id.action_boardFragment_to_boardDetailFragment, bundle)
            }
        }

    }



    private fun makeCategoryShimmerJob() = lifecycleScope.launch {
        binding.fragBoardCategoryRecycler.visibility = View.GONE
        binding.fragBoardCategoryLayoutShimmer.visibility = View.VISIBLE
        binding.fragBoardCategoryLayoutShimmer.startShimmer()
        delay(1500)
    }

    private fun makeBoardShimmerJob() = lifecycleScope.launch {
        binding.fragBoardBodyRecycler.visibility = View.GONE
        binding.fragBoardBodyShimmerLayout.visibility = View.VISIBLE
        binding.fragBoardBodyShimmerLayout.startShimmer()
        delay(1500)
    }

    private fun showCategory() {
        showCategoryShimmer?.let {
            if (it.isActive) {
                it.invokeOnCompletion {
                    binding.fragBoardCategoryRecycler.visibility = View.VISIBLE
                    binding.fragBoardCategoryLayoutShimmer.stopShimmer()
                    binding.fragBoardCategoryLayoutShimmer.visibility = View.GONE
                    showCategoryShimmer = null
                }
            } else {
                binding.fragBoardCategoryRecycler.visibility = View.VISIBLE
                binding.fragBoardCategoryLayoutShimmer.stopShimmer()
                binding.fragBoardCategoryLayoutShimmer.visibility = View.GONE
                showCategoryShimmer = null
            }
        }
    }

    private fun showBoard() {
        showBoardShimmer?.let {
            if (it.isActive) {
                it.invokeOnCompletion {
                    binding.fragBoardBodyRecycler.visibility = View.VISIBLE
                    binding.fragBoardBodyShimmerLayout.stopShimmer()
                    binding.fragBoardBodyShimmerLayout.visibility = View.GONE
                    showBoardShimmer = null
                }
            } else {
                binding.fragBoardBodyRecycler.visibility = View.VISIBLE
                binding.fragBoardBodyShimmerLayout.stopShimmer()
                binding.fragBoardBodyShimmerLayout.visibility = View.GONE
                showBoardShimmer = null
            }
        }
    }
}