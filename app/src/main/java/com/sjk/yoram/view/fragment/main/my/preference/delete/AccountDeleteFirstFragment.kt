package com.sjk.yoram.view.fragment.main.my.preference.delete

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sjk.yoram.databinding.FragMyPrefAccountDeleteFirstBinding
import com.sjk.yoram.viewmodel.AccountDeleteViewModel

class AccountDeleteFirstFragment: Fragment() {
    private lateinit var binding: FragMyPrefAccountDeleteFirstBinding
    private lateinit var viewModel: AccountDeleteViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragMyPrefAccountDeleteFirstBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireParentFragment().requireParentFragment())[AccountDeleteViewModel::class.java]
        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }
}