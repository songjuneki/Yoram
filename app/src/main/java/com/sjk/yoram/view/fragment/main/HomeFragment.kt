package com.sjk.yoram.view.fragment.main

import androidx.fragment.app.activityViewModels
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragMainHomeBinding
import com.sjk.yoram.model.YoramFragment
import com.sjk.yoram.viewmodel.MainViewModel

class HomeFragment: YoramFragment<FragMainHomeBinding>(R.layout.frag_main_home) {
    private val mainViewModel: MainViewModel by activityViewModels()
    override fun init() {

    }
}