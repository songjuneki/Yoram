package com.sjk.yoram.view.fragment.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.navGraphViewModels
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragHomeBinding
import com.sjk.yoram.model.ui.adapter.HomeBannerAdapter
import com.sjk.yoram.viewmodel.FragHomeViewModel
import com.sjk.yoram.viewmodel.MainViewModel

class HomeFragment: Fragment() {
    private lateinit var binding: FragHomeBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val homeViewModel: FragHomeViewModel by navGraphViewModels(R.id.navi_home) { FragHomeViewModel.Factory(requireActivity().application) }

    private var backPressedTime = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_home, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.mainVM = mainViewModel
        binding.homeVM = homeViewModel

        binding.homeBannerPager.adapter = HomeBannerAdapter()
        binding.homeBannerIndicator.setViewPager2(binding.homeBannerPager)

        initBackPressedDispatcher()

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