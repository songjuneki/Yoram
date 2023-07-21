package com.sjk.yoram.view.fragment.main

import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragMainIdBinding
import com.sjk.yoram.model.YoramFragment
import com.sjk.yoram.viewmodel.MainViewModel

class IdFragment: YoramFragment<FragMainIdBinding>(R.layout.frag_main_id) {
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun init() {

    }

    fun getNavController(): NavController = binding.fragMainIdFrame.findNavController()
}