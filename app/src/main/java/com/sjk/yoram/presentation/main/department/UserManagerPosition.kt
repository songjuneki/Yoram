package com.sjk.yoram.presentation.main.department

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sjk.yoram.databinding.FragUserManagerPosBinding

class UserManagerPosition: Fragment() {
    private lateinit var binding: FragUserManagerPosBinding
    private val viewModel: FragDptmentViewModel by viewModels(ownerProducer = { requireParentFragment().requireParentFragment().parentFragmentManager.fragments.first()  })
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragUserManagerPosBinding.inflate(layoutInflater)

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
    }
}