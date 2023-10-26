package com.sjk.yoram.presentation.main.department.manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.sjk.yoram.databinding.FragUserManagerHomeBinding
import com.sjk.yoram.presentation.main.MainViewModel
import com.sjk.yoram.presentation.main.department.FragDptmentViewModel

class UserManagerHome: Fragment() {
    private lateinit var binding: FragUserManagerHomeBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: FragDptmentViewModel by viewModels(ownerProducer = { requireParentFragment().requireParentFragment().parentFragmentManager.fragments.first()  })
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragUserManagerHomeBinding.inflate(layoutInflater)

        binding.mainVM = mainViewModel
        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }
}