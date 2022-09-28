package com.sjk.yoram.view.fragment.main.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragMyPrefPrivacyBinding
import com.sjk.yoram.viewmodel.FragPrivacyViewModel
import com.sjk.yoram.viewmodel.PrefViewModel

class PrefPrivacyFragment: Fragment() {
    private lateinit var binding: FragMyPrefPrivacyBinding
    private lateinit var viewModel: FragPrivacyViewModel
    private lateinit var prefViewModel: PrefViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_my_pref_privacy, container, false)
        viewModel = ViewModelProvider(requireActivity())[FragPrivacyViewModel::class.java]
        prefViewModel = ViewModelProvider(requireActivity())[PrefViewModel::class.java]
        binding.vm = viewModel
        binding.prefVM = prefViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.exitEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().popBackStack(R.id.prefFragment, false)
            }
        }
    }

}