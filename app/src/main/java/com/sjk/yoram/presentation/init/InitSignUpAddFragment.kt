package com.sjk.yoram.presentation.init

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragInitSignupAddBinding
import com.sjk.yoram.presentation.common.model.LoginState
import com.sjk.yoram.presentation.main.MainActivity

class InitSignUpAddFragment: Fragment() {
    private lateinit var binding: FragInitSignupAddBinding
    private lateinit var viewModel: InitViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_init_signup_add, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(InitViewModel::class.java)
        binding.vm = viewModel
        binding.lifecycleOwner = this

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
                if (it == LoginState.LOGIN) {
                    val main = Intent(requireContext(), MainActivity::class.java)
                    main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    main.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    requireActivity().finish()
                    startActivity(main)
                    requireActivity().overridePendingTransition(0, 0)
                }
            }
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