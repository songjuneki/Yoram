package com.sjk.yoram.presentation.main.my.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sjk.yoram.R
import com.sjk.yoram.databinding.DialogEditBirthdayBinding
import java.text.SimpleDateFormat
import java.util.Locale

class EditBDDialogFragment: BottomSheetDialogFragment() {
    private val viewModel: EditViewModel by activityViewModels()
    private lateinit var binding: DialogEditBirthdayBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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