package com.sjk.yoram.presentation.main.department.manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragUserManagerGiveBinding
import com.sjk.yoram.presentation.main.department.FragDptmentViewModel

class UserManagerGive: Fragment() {
    private lateinit var binding: FragUserManagerGiveBinding
    private val viewModel: FragDptmentViewModel by viewModels(ownerProducer = { requireParentFragment().requireParentFragment().parentFragmentManager.fragments.first()  })
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragUserManagerGiveBinding.inflate(layoutInflater)

        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userManagerBackEvent.observe(this.viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigateUp()
            }
        }

        viewModel.detailGiveEvent.observe(this.viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(R.id.action_userManagerGive_to_userManagerGiveDetail)
            }
        }
    }
}