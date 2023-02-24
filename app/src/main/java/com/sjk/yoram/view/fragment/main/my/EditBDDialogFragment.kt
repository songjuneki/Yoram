package com.sjk.yoram.view.fragment.main.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sjk.yoram.R
import com.sjk.yoram.databinding.DialogEditBirthdayBinding
import com.sjk.yoram.viewmodel.EditViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class EditBDDialogFragment: BottomSheetDialogFragment() {
    private lateinit var viewModel: EditViewModel
    private lateinit var binding: DialogEditBirthdayBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[EditViewModel::class.java]
        binding = DialogEditBirthdayBinding.inflate(layoutInflater)
        binding.vm = viewModel
        initView()

        return binding.root
    }

    private fun initView() {
        var bd = viewModel.user.value?.birth ?: ""
        if (bd.length < 9)
            bd = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(System.currentTimeMillis())
        val year = bd.substring(0, 4).toInt()
        val month = bd.substring(5, 7).toInt()
        val day = bd.substring(8, 10).toInt()
        binding.dialogEditBdPicker.updateDate(year, month-1, day)
        binding.dialogEditBdOk.setOnClickListener {
            viewModel.setBD("${binding.dialogEditBdPicker.year}-${String.format("%02d", binding.dialogEditBdPicker.month+1)}-${String.format("%02d", binding.dialogEditBdPicker.dayOfMonth)}")
            dismiss()
        }
    }

    override fun getTheme(): Int = R.style.DraggableRoundedBottomSheetDialog
}