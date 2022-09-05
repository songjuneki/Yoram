package com.sjk.yoram.view.fragment.main.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sjk.yoram.R
import com.sjk.yoram.databinding.DialogAddressBinding
import com.sjk.yoram.databinding.DialogEditAddressBinding
import com.sjk.yoram.model.ui.adapter.AddressListAdapter
import com.sjk.yoram.model.ui.decorator.SimpleItemDivider
import com.sjk.yoram.model.ui.listener.AddressItemClickListener
import com.sjk.yoram.viewmodel.EditViewModel
import com.sjk.yoram.viewmodel.InitViewModel

class EditAddressDialogFragment: BottomSheetDialogFragment() {
    private lateinit var viewModel: EditViewModel
    private lateinit var binding: DialogEditAddressBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity())[EditViewModel::class.java]
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