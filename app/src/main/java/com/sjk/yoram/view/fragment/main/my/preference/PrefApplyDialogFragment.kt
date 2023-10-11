package com.sjk.yoram.view.fragment.main.my.preference

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sjk.yoram.R
import com.sjk.yoram.databinding.DialogMyPrefApplyBinding
import com.sjk.yoram.viewmodel.PrefViewModel

class PrefApplyDialogFragment: BottomSheetDialogFragment() {
    private lateinit var binding: DialogMyPrefApplyBinding
    private lateinit var viewModel: PrefViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogMyPrefApplyBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity())[PrefViewModel::class.java]

        binding.prefVM = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun getTheme(): Int = R.style.DraggableRoundedBottomSheetDialog
}