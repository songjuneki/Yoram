package com.sjk.yoram.view.fragment.main.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragMyPrefAdminGiveBinding
import com.sjk.yoram.viewmodel.PrefViewModel

class AdminGiveTypeFragment: Fragment() {
    private lateinit var binding: FragMyPrefAdminGiveBinding
    private lateinit var prefViewModel: PrefViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragMyPrefAdminGiveBinding.inflate(layoutInflater)
        prefViewModel = ViewModelProvider(requireActivity())[PrefViewModel::class.java]
        binding.prefVM = prefViewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefViewModel.detailGiveTypeEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(R.id.action_adminGiveTypeFragment_to_editGiveTypeDialogFragment)
            }
        }

        prefViewModel.exitGiveTypeEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(R.id.action_prefApplyDialogFragment_to_prefFragment)
            }
        }
    }

}