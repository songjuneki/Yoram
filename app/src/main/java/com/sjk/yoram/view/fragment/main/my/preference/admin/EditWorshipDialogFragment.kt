package com.sjk.yoram.view.fragment.main.my.preference.admin

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sjk.yoram.databinding.DialogMyAdminWorshipEditBinding
import com.sjk.yoram.viewmodel.PrefViewModel

class EditWorshipDialogFragment: BottomSheetDialogFragment() {
    private lateinit var viewModel: PrefViewModel
    private lateinit var binding: DialogMyAdminWorshipEditBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity())[PrefViewModel::class.java]
        binding = DialogMyAdminWorshipEditBinding.inflate(layoutInflater)
        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.deleteWorshipEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                val dialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("삭제하시겠습니까?")
                    setMessage("선택한 예배를 삭제합니다.")
                    setPositiveButton("삭제", it)
                    setNegativeButton("취소") { p0, _ -> p0.dismiss()}
                }
                dialog.show()
            }
        }
        viewModel.usingWorshipEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                val dialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("오류")
                    setMessage("사용중인 예배 타입입니다. 수정만 가능합니다.")
                    setPositiveButton("확인") { p0, _ -> p0.dismiss()}
                }
                dialog.show()
            }
        }
    }
}