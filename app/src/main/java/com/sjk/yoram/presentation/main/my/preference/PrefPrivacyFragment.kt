package com.sjk.yoram.presentation.main.my.preference

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragMyPrefPrivacyBinding

class PrefPrivacyFragment: Fragment() {
    private lateinit var binding: FragMyPrefPrivacyBinding
    private lateinit var viewModel: FragPrivacyViewModel
    private lateinit var prefViewModel: PrefViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_my_pref_privacy, container, false)
        viewModel = ViewModelProvider(requireActivity())[FragPrivacyViewModel::class.java]
        prefViewModel = ViewModelProvider(requireActivity())[PrefViewModel::class.java]
        binding.vm = viewModel
        binding.prefVM = prefViewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        viewModel.loadPP()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.applyEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(R.id.action_prefPrivacyFragment_to_prefApplyDialogFragment)
            }
        }

        viewModel.applySuccessEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(R.id.action_prefApplyDialogFragment_to_prefFragment)
            }
        }

        viewModel.applyFailureEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                val dialog = AlertDialog.Builder(requireContext())
                dialog.setTitle("오류")
                    .setMessage("적용하는 중에 오류가 발생했습니다. 다시 시도해 주세요")
                    .setPositiveButton("확인") { d, _ -> d.dismiss()}
                    .show()
            }
        }
    }

}