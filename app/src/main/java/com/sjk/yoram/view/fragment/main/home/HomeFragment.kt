package com.sjk.yoram.view.fragment.main.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.navGraphViewModels
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragHomeBinding
import com.sjk.yoram.model.YoramFragment
import com.sjk.yoram.model.ui.adapter.HomeBannerAdapter
import com.sjk.yoram.viewmodel.FragHomeViewModel
import com.sjk.yoram.viewmodel.MainViewModel

class HomeFragment: YoramFragment<FragHomeBinding>(R.layout.frag_home) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val homeViewModel: FragHomeViewModel by navGraphViewModels(R.id.navi_home) { FragHomeViewModel.Factory(requireActivity().application) }

    override fun init() {
        binding.mainVM = mainViewModel
        binding.homeVM = homeViewModel

        binding.homeBannerPager.adapter = HomeBannerAdapter()
        binding.homeBannerIndicator.setViewPager2(binding.homeBannerPager)
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