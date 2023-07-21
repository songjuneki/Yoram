package com.sjk.yoram.view.fragment.main.my.preference.delete

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sjk.yoram.R
import com.sjk.yoram.databinding.DialogAccountDeleteBirthdayBinding
import com.sjk.yoram.viewmodel.AccountDeleteViewModel

class AccountDeleteBirthEditDialog: BottomSheetDialogFragment() {
    private lateinit var binding: DialogAccountDeleteBirthdayBinding
    private lateinit var viewModel: AccountDeleteViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogAccountDeleteBirthdayBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireParentFragment().requireParentFragment())[AccountDeleteViewModel::class.java]
        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }

    override fun getTheme(): Int = R.style.DraggableRoundedBottomSheetDialog
}