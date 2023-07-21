package com.sjk.yoram.view.fragment.main.my.preference.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragMyPrefAdminDepartmentBinding
import com.sjk.yoram.viewmodel.AdminDepartmentViewModel
import com.sjk.yoram.viewmodel.PrefViewModel

class AdminDepartmentFragment: Fragment() {
    private lateinit var binding: FragMyPrefAdminDepartmentBinding
    private lateinit var prefViewModel: PrefViewModel
    private lateinit var viewModel: AdminDepartmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragMyPrefAdminDepartmentBinding.inflate(layoutInflater)
        prefViewModel = ViewModelProvider(requireActivity())[PrefViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity())[AdminDepartmentViewModel::class.java]
        binding.prefVM = prefViewModel
        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        viewModel.loadDepartmentList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.detailDepartmentEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(R.id.action_adminDepartmentFragment_to_editDepartmentDialogFragment)
            }
        }

        viewModel.exitEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().popBackStack(R.id.prefFragment, false)
            }
        }

        viewModel.applyEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(R.id.action_adminDepartmentFragment_to_prefApplyDialogFragment)
            }
        }

        viewModel.applyFailEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(requireContext(), "적용에 실패했습니다. 잠시 후 다시 시도해주세요", Toast.LENGTH_LONG).show()
            }
        }
    }
}