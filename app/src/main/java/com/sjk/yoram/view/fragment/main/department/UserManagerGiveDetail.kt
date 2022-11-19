package com.sjk.yoram.view.fragment.main.department

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sjk.yoram.databinding.FragUserManagerGiveBinding
import com.sjk.yoram.databinding.FragUserManagerGiveDetailBinding
import com.sjk.yoram.databinding.FragUserManagerPermissionBinding
import com.sjk.yoram.viewmodel.FragDptmentViewModel

class UserManagerGiveDetail: Fragment() {
    private lateinit var binding: FragUserManagerGiveDetailBinding
    private lateinit var dptViewModel: FragDptmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragUserManagerGiveDetailBinding.inflate(layoutInflater)
        dptViewModel = ViewModelProvider(requireActivity())[FragDptmentViewModel::class.java]

        binding.vm = dptViewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dptViewModel.userManagerBackEvent.observe(this.viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigateUp()
            }
        }
    }
}