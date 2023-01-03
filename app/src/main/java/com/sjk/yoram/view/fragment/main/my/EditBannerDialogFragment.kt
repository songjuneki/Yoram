package com.sjk.yoram.view.fragment.main.my

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sjk.yoram.R
import com.sjk.yoram.databinding.DialogMyAdminBannerEditBinding
import com.sjk.yoram.viewmodel.AdminBannerViewModel

class EditBannerDialogFragment: BottomSheetDialogFragment() {
    private lateinit var viewModel: AdminBannerViewModel
    private lateinit var binding: DialogMyAdminBannerEditBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        viewModel = ViewModelProvider(requireParentFragment().childFragmentManager.fragments[0])[AdminBannerViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity())[AdminBannerViewModel::class.java]
        binding = DialogMyAdminBannerEditBinding.inflate(layoutInflater)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.editDeleteEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                val dialog = AlertDialog.Builder(requireContext())
                dialog.setTitle("삭제하시겠습니까?").setMessage("이 작업은 취소 될 수 없습니다.")
                    .setPositiveButton("삭제", it)
                    .setNegativeButton("취소") { p0, _ ->
                        p0.dismiss()
                    }
                    .show()
            }
        }

        viewModel.editCancelEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                this.dismiss()
            }
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isFitToContents = true
            behavior.isDraggable = false
            behavior.skipCollapsed = true
        }
        return dialog
    }

    override fun getTheme(): Int = R.style.FullExpandedRoundedNoCloseBottomSheetDialog
}