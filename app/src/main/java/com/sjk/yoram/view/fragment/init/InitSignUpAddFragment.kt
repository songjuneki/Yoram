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
import com.sjk.yoram.databinding.FragInitSignupAddBinding
import com.sjk.yoram.viewmodel.InitViewModel

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

        viewModel.naviAction.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(it)
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