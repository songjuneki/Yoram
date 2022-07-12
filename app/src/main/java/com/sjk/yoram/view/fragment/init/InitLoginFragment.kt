package com.sjk.yoram.view.fragment.init

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragInitLoginBinding
import com.sjk.yoram.viewmodel.InitViewModel

class InitLoginFragment: Fragment(R.layout.frag_init_login) {
    private lateinit var binding: FragInitLoginBinding
//    private val viewModel by lazy { ViewModelProvider(requireActivity(), InitViewModel.Factory(requireActivity() as Application))[InitViewModel::class.java]}
    private lateinit var viewModel: InitViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_init_login, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(InitViewModel::class.java)
        binding.vm = viewModel
//        binding.lifecycleOwner = requireActivity()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}