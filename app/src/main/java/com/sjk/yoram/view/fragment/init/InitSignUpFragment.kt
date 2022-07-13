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
import com.sjk.yoram.databinding.FragInitSignupBinding
import com.sjk.yoram.viewmodel.InitViewModel

class InitSignUpFragment: Fragment(R.layout.frag_init_signup) {
    private lateinit var binding: FragInitSignupBinding
    private lateinit var viewModel: InitViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_init_signup, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(InitViewModel::class.java)
        binding.vm = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}