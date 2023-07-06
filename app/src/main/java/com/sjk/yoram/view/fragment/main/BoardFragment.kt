package com.sjk.yoram.view.fragment.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragBoardBinding
import com.sjk.yoram.model.ApiState
import com.sjk.yoram.viewmodel.FragBoardViewModel
import com.sjk.yoram.viewmodel.MainViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class BoardFragment: Fragment() {
    private lateinit var binding: FragBoardBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var viewModel: FragBoardViewModel

    private var showCategoryShimmer: Job? = null
    private var showBoardShimmer: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_board, container, false)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity())[FragBoardViewModel::class.java]

        binding.mainVM = mainViewModel
        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.categoryList.collectLatest {
                when (it) {
                    is ApiState.Loading -> showCategoryLoading()
                    is ApiState.Success ->  {
                        makeCategoryShimmerJob().join()
                        showCategory()
                    }
                    is ApiState.Error -> {
                        // 카테고리 에러 뷰 핸들링
                    }
                }
            }
        }

    }


    private fun showCategory() {
        binding.fragBoardCategoryLayoutShimmer.stopShimmer()
        binding.fragBoardCategoryLayoutShimmer.visibility = View.GONE
        binding.fragBoardCategoryRecycler.visibility = View.VISIBLE
    }

    private fun showCategoryLoading() {
        binding.fragBoardCategoryRecycler.visibility = View.GONE
        binding.fragBoardCategoryLayoutShimmer.startShimmer()
        binding.fragBoardCategoryLayoutShimmer.visibility = View.VISIBLE
    }

    private fun makeCategoryShimmerJob() = lifecycleScope.launch {
        binding.fragBoardCategoryRecycler.visibility = View.GONE
        binding.fragBoardCategoryLayoutShimmer.startShimmer()
        binding.fragBoardCategoryLayoutShimmer.visibility = View.VISIBLE
        delay(3000)
    }
}