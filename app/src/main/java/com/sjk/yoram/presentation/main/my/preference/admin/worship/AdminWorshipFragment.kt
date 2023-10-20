package com.sjk.yoram.presentation.main.my.preference.admin.worship

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragMyPrefAdminWorshipBinding
import com.sjk.yoram.presentation.main.my.preference.PrefViewModel

class AdminWorshipFragment: Fragment() {
    private lateinit var binding: FragMyPrefAdminWorshipBinding
    private lateinit var prefViewModel: PrefViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragMyPrefAdminWorshipBinding.inflate(layoutInflater)
        prefViewModel = ViewModelProvider(requireActivity())[PrefViewModel::class.java]
        binding.prefVM = prefViewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefViewModel.detailWorshipEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(R.id.action_adminWorshipFragment_to_editWorshipDialogFragment)
            }
        }

        prefViewModel.exitWorshipEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(R.id.action_prefApplyDialogFragment_to_prefFragment)
            }
        }
    }

}