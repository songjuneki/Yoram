package com.sjk.yoram.view.fragment.init

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragInitMainBinding
import com.sjk.yoram.view.activity.MainActivity
import com.sjk.yoram.viewmodel.InitViewModel

class InitHomeFragment: Fragment() {
    private lateinit var binding: FragInitMainBinding
    private lateinit var viewModel: InitViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_init_main, container, false)
        viewModel =  ViewModelProvider(requireActivity()).get(InitViewModel::class.java)
        binding.vm = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        viewModel.initialize()
    }

}