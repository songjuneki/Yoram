package com.sjk.yoram.view.fragment.main.department

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sjk.yoram.databinding.FragUserManagerHomeBinding
import com.sjk.yoram.viewmodel.FragDptmentViewModel

class UserManagerHome: Fragment() {
    private lateinit var binding: FragUserManagerHomeBinding
    private lateinit var dptViewModel: FragDptmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragUserManagerHomeBinding.inflate(layoutInflater)
        dptViewModel = ViewModelProvider(requireActivity())[FragDptmentViewModel::class.java]

        binding.vm = dptViewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }
}