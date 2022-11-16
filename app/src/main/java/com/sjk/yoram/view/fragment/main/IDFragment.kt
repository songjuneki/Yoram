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
import com.sjk.yoram.databinding.FragIdBinding
import com.sjk.yoram.viewmodel.FragIDViewModel

class IDFragment: Fragment() {
    private lateinit var binding: FragIdBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var viewModel: FragIDViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_id, container, false)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity())[FragIDViewModel::class.java]
        binding.mainVM = mainViewModel
        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.backEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { findNavController().popBackStack(R.id.iDFragment, false) }
        }
        viewModel.navEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(it)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.countStop()
    }

}