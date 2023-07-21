package com.sjk.yoram.view.fragment.main.my.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sjk.yoram.R
import com.sjk.yoram.databinding.DialogEditAddressBinding
import com.sjk.yoram.model.ui.decorator.SimpleItemDivider
import com.sjk.yoram.viewmodel.EditViewModel

class EditAddressDialogFragment: BottomSheetDialogFragment() {
    private val viewModel: EditViewModel by activityViewModels()
    private lateinit var binding: DialogEditAddressBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogEditAddressBinding.inflate(layoutInflater)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        initView()

        return binding.root
    }

    private fun initView() {
        binding.dialogEditAddressRecycler.addItemDecoration(SimpleItemDivider(this.requireContext(), LinearLayoutManager.VERTICAL))
    }


    override fun getTheme(): Int = R.style.RoundedBottomSheetDialog


    override fun onStop() {
        super.onStop()
        binding.lifecycleOwner = null
    }
}