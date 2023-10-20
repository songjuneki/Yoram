package com.sjk.yoram.presentation.main.my.preference.admin.department

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sjk.yoram.R
import com.sjk.yoram.databinding.DialogMyAdminDepartmentEditBinding
import com.sjk.yoram.presentation.main.my.preference.PrefViewModel

class EditDepartmentDialogFragment: BottomSheetDialogFragment() {
    private lateinit var binding: DialogMyAdminDepartmentEditBinding
    private lateinit var prefViewModel: PrefViewModel
    private lateinit var viewModel: AdminDepartmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogMyAdminDepartmentEditBinding.inflate(layoutInflater)
        prefViewModel = ViewModelProvider(requireActivity())[PrefViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity())[AdminDepartmentViewModel::class.java]

        binding.prefVM = prefViewModel
        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.backEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().popBackStack(R.id.adminDepartmentFragment, false)
            }
        }

        viewModel.applyDetailEditEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigateUp()
            }
        }

        viewModel.deleteEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                val dialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("삭제하시겠습니까?")
                    setMessage("선택한 부서를 삭제합니다.")
                    setPositiveButton("삭제", it)
                    setNegativeButton("취소") { d, _ -> d.dismiss()}
                }
                dialog.show()
            }
        }

        viewModel.isUsingDepartment.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                val dialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("오류")
                    setMessage("사용중인 부서입니다. 수정만 가능합니다.")
                    setPositiveButton("확인") { d, _ -> d.dismiss() }
                }
                dialog.show()
            }
        }
    }
}