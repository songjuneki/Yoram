package com.sjk.yoram.view.fragment.init

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sjk.yoram.R
import com.sjk.yoram.databinding.DialogAgreeBinding
import com.sjk.yoram.model.InitFragmentType
import com.sjk.yoram.viewmodel.InitViewModel

class InitAgreeDialogFragment: BottomSheetDialogFragment() {
    private lateinit var viewModel: InitViewModel
    private lateinit var binding: DialogAgreeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity()).get(InitViewModel::class.java)
        binding = DialogAgreeBinding.inflate(layoutInflater)
        binding.vm = viewModel
        initView()

        return binding.root
    }

    private fun initView() {
        binding.dialogAgreeRuleTv.text = if(viewModel.currentFragment.value!! == InitFragmentType.InitFragment_Dialog_APP_RULE) getString(R.string.app_policy_rule) else getString(R.string.privacy_policy_rule)
    }


    override fun getTheme(): Int = R.style.ExpandedRoundedBottomSheetDialog

}