package com.sjk.yoram.view.fragment.init

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sjk.yoram.R
import com.sjk.yoram.databinding.DialogBirthdayBinding
import com.sjk.yoram.viewmodel.InitViewModel
import java.text.SimpleDateFormat

class InitBDDialogFragment: BottomSheetDialogFragment() {
    private lateinit var viewModel: InitViewModel
    private lateinit var binding: DialogBirthdayBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity()).get(InitViewModel::class.java)
        binding = DialogBirthdayBinding.inflate(layoutInflater)
        initView()

        return binding.root
    }

    private fun initView() {
        var bd = viewModel.newBD.value ?: ""
        if (bd.length < 9)
            bd = SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis())
        val year = bd.substring(0, 4).toInt()
        val month = bd.substring(5, 7).toInt()
        val day = bd.substring(8, 10).toInt()
        binding.dialogBdPicker.updateDate(year, month-1, day)
        binding.dialogBdCancel.setOnClickListener { dismiss() }
        binding.dialogBdOk.setOnClickListener {
            viewModel.setBD("${binding.dialogBdPicker.year}-${String.format("%02d", binding.dialogBdPicker.month+1)}-${binding.dialogBdPicker.dayOfMonth}")
            dismiss()
        }
    }

    override fun getTheme(): Int = R.style.RoundedBottomSheetDialog
}