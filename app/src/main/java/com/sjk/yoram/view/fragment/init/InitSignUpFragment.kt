package com.sjk.yoram.view.fragment.init

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragInitSignupBinding
import com.sjk.yoram.viewmodel.InitViewModel

class InitSignUpFragment: Fragment() {
    private lateinit var binding: FragInitSignupBinding
    private lateinit var viewModel: InitViewModel
    private val korNameRegex = Regex("^[가-힣]+$")
    private val pwRegex = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_init_signup, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(InitViewModel::class.java)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.naviAction.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(it)
            }
        }

        viewModel.newName.observe(viewLifecycleOwner) { name ->
            if (name.isEmpty())
                binding.initSignupNameEtLayout.error = ""
            else if (name.length == 1)
                binding.initSignupNameEtLayout.error = "두 글자 이상 입력해주세요"
            else if (!name.matches(korNameRegex))
                binding.initSignupNameEtLayout.error = "한글만 입력해주세요"
            else
                binding.initSignupNameEtLayout.error = ""
        }

        viewModel.newPw.observe(viewLifecycleOwner) { pw ->
            if (pw.isEmpty() || pw.matches(pwRegex))
                binding.initSignupPwEtLayout.error = ""
            else
                binding.initSignupPwEtLayout.error = "영문+숫자 8자리 이상 입력해주세요"
        }

        viewModel.newPwV.observe(viewLifecycleOwner) { pw ->
            if (pw.isEmpty())
                binding.initSignupPwvEtLayout.error = ""
            else if (pw == viewModel.newPw.value)
                binding.initSignupPwvEtLayout.error = ""
            else
                binding.initSignupPwvEtLayout.error = "비밀번호가 일치하지 않습니다."
        }
    }

    override fun onResume() {
        super.onResume()
        binding.lifecycleOwner = this
    }

    override fun onStop() {
        super.onStop()
        binding.lifecycleOwner = null
    }
}