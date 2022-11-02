package com.sjk.yoram.view.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sjk.yoram.viewmodel.MainViewModel
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragDptmentBinding
import com.sjk.yoram.viewmodel.FragDptmentViewModel

class DptmentFragment: Fragment() {
    private lateinit var binding: FragDptmentBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var viewModel: FragDptmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_dptment, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(FragDptmentViewModel::class.java)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        binding.fragDptmentHeaderSpinner.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.dptmentFragment)
                viewModel.hideSearchbar()
        }

        viewModel.userDetailEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                if (findNavController().currentDestination?.id == R.id.dptmentFragment)
                    findNavController().navigate(R.id.action_dptFragment_to_userInfoDialog)
            }
        }

        viewModel.searchEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                binding.fragDptmentHeaderSearchbar.requestFocus()
            }
        }

        mainViewModel.goDptSearchEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                binding.fragDptmentHeaderSearch.callOnClick()
            }
        }
    }

}