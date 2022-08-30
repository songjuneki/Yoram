package com.sjk.yoram.view.fragment.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sjk.yoram.R
import com.sjk.yoram.viewmodel.MainViewModel
import com.sjk.yoram.databinding.FragMyBinding
import com.sjk.yoram.viewmodel.FragMyViewModel

class MyFragment: Fragment() {
    private lateinit var binding: FragMyBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var viewModel: FragMyViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_my, container, false)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity())[FragMyViewModel::class.java]
        binding.mainVM = mainViewModel
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}