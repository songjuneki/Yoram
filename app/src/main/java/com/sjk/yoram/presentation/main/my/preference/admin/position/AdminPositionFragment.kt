package com.sjk.yoram.presentation.main.my.preference.admin.position

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragMyPrefAdminPositionBinding
import com.sjk.yoram.presentation.main.my.preference.PrefViewModel

class AdminPositionFragment: Fragment() {
    private lateinit var binding: FragMyPrefAdminPositionBinding
    private lateinit var prefViewModel: PrefViewModel
    private lateinit var viewModel: AdminPositionViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragMyPrefAdminPositionBinding.inflate(layoutInflater)
        prefViewModel = ViewModelProvider(requireActivity())[PrefViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity())[AdminPositionViewModel::class.java]
        binding.prefVM = prefViewModel
        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        viewModel.loadPositionList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.detailPositionEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(R.id.action_adminPositionFragment_to_editPositionDialogFragment)
            }
        }

        viewModel.exitEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().popBackStack(R.id.prefFragment, false)
            }
        }

        viewModel.applyEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(R.id.action_adminPositionFragment_to_prefApplyDialogFragment)
            }
        }

        viewModel.applyFailEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(requireContext(), "적용에 실패했습니다. 잠시 후 다시 시도해주세요", Toast.LENGTH_LONG).show()
            }
        }
    }
}