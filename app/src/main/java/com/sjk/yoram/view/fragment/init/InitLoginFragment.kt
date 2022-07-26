package com.sjk.yoram.view.fragment.init

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragInitLoginBinding
import com.sjk.yoram.model.LoginState
import com.sjk.yoram.view.activity.MainActivity
import com.sjk.yoram.viewmodel.InitViewModel

class InitLoginFragment: Fragment() {
    private lateinit var binding: FragInitLoginBinding
    private lateinit var viewModel: InitViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_init_login, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(InitViewModel::class.java)
        binding.vm = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModel.naviAction.observe(viewLifecycleOwner) { event ->
//            event.getContentIfNotHandled()?.let {
//                findNavController().navigate(it)
//            }
//        }

        viewModel.loginEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                if (it == LoginState.PW_FAIL) binding.initLoginPwEtLayout.error = "비밀번호를 확인해주세요"
                if (it == LoginState.NAME_FAIL) binding.initLoginNameEtLayout.error = "이름을 확인해주세요"
            }
        }

    }
}