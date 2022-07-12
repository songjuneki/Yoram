package com.sjk.yoram.view.fragment.main

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.sjk.yoram.viewmodel.MainViewModel
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragHomeBinding
import com.sjk.yoram.model.adapter.HomeBannerAdapter
import com.sjk.yoram.viewmodel.FragDptmentViewModel
import com.sjk.yoram.viewmodel.FragHomeViewModel

class HomeFragment: Fragment(R.layout.frag_home) {
    private lateinit var binding: FragHomeBinding
    private lateinit var viewModel: FragHomeViewModel
//    private val viewModel: FragHomeViewModel by lazy { ViewModelProvider(this, FragHomeViewModel.Factory(requireActivity().application))[FragHomeViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_home, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(FragHomeViewModel::class.java)
        binding.vm = viewModel
//        binding.lifecycleOwner = requireActivity()

        binding.homeBannerPager.adapter = HomeBannerAdapter()
        binding.homeBannerIndicator.setViewPager2(binding.homeBannerPager)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // home banner pager 상호작용
        viewModel.bannerList.observe(viewLifecycleOwner) {
            (binding.homeBannerPager.adapter as HomeBannerAdapter).fetchBanner(it)
        }

        // 조직도 검색 클릭 이벤트
        // 내 정보 상호작용
        // 교회 소식 상호작용


//        val adapter = CardAdapter()
//        val layoutManager = LinearLayoutManager(this.context)
//        layoutManager.orientation = LinearLayoutManager.VERTICAL
//
//
//        adapter.setOnDptItemClickListener(object: CardAdapter.OnDptItemClickListener {
//            override fun onItemClick(type: DptButtonType, dptCode: Int) {
//                CoroutineScope(Dispatchers.Main).async {
//                    mainViewModel.moveDptFrag()
//                    dptFragViewModel.sortAndExpandDepartment(type, dptCode)
//                }
//            }
//        })
//        adapter.setOnSearchBarClickListener(object: CardAdapter.OnSearchBarClickListener {
//            override fun onSearchBarClick() {
//                // change department fragment and focus searchbar
//                mainViewModel.moveDptFrag()
//                dptFragViewModel.isMoved.value = true
//            }
//        })
//
//        binding.vm = viewModel
//        binding.fragHomeRecycler.adapter = adapter
//        binding.fragHomeRecycler.layoutManager = layoutManager
//
//
//        viewModel.cards.observe(viewLifecycleOwner, Observer {
//            (binding.fragHomeRecycler.adapter as CardAdapter).fetchCard(it)
//        })
//
//        viewModel.rootDpts.observe(viewLifecycleOwner, Observer {
//            (binding.fragHomeRecycler.adapter as CardAdapter).fetchDpts(it)
//        })
//
//
//        var user = mainViewModel.loginData.value ?: MyLoginData()
//        Log.d("JKJK", "HomeFrag -- loginData=${user}")
//        viewModel.addCard(Card(CardType.HOME_BANNER))
//        viewModel.addCard(Card(CardType.HOME_DEPARTMENT))
//        Log.d("JKJK", "HomeFrag -- loginState=${mainViewModel.loginState}")
//        viewModel.addCard(Card(CardType.HOME_ID, userData(user)))
//
//        mainViewModel.loginState.observe(viewLifecycleOwner, Observer {
//            user = mainViewModel.loginData.value ?: MyLoginData()
//            viewModel.modifyCard(2, Card(CardType.HOME_ID, userData(user)))
//        })

    }
}