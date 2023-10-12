package com.sjk.yoram.view.fragment.init

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragInitLoginBinding
import com.sjk.yoram.model.LoginState
import com.sjk.yoram.view.activity.main.MainActivity
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
        binding.lifecycleOwner = this.viewLifecycleOwner

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
                when (it) {
                    LoginState.PW_FAIL -> binding.initLoginPwEtLayout.error = "비밀번호가 일치하지 않습니다"
                    LoginState.NAME_FAIL -> binding.initLoginNameEtLayout.error = "이름을 확인해주세요"
                    LoginState.LOGIN -> {
                        val main = Intent(requireContext(), MainActivity::class.java)
                        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        main.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        requireActivity().finish()
                        startActivity(main)
                        requireActivity().overridePendingTransition(0, 0)
                    }
                    else -> return@let
                }
            }
        }

    }
}