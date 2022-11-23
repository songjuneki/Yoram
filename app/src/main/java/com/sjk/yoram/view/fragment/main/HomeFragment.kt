package com.sjk.yoram.view.fragment.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragHomeBinding
import com.sjk.yoram.model.ui.adapter.HomeBannerAdapter
import com.sjk.yoram.viewmodel.FragHomeViewModel
import com.sjk.yoram.viewmodel.MainViewModel

class HomeFragment: Fragment() {
    private lateinit var binding: FragHomeBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var homeViewModel: FragHomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_home, container, false)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        homeViewModel = ViewModelProvider(requireActivity()).get(FragHomeViewModel::class.java)
        binding.mainVM = mainViewModel
        binding.homeVM = homeViewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        binding.homeBannerPager.adapter = HomeBannerAdapter()
        binding.homeBannerIndicator.setViewPager2(binding.homeBannerPager)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.bannerList.observe(viewLifecycleOwner) {
            (binding.homeBannerPager.adapter as HomeBannerAdapter).fetchBanner(it)
        }
    }

    override fun onResume() {
        homeViewModel.loadBanners()
        mainViewModel.loadLoginData()
        mainViewModel.loadGiveAmount()
        super.onResume()
    }
}