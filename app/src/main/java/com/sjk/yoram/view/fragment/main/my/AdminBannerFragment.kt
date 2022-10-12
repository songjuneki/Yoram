package com.sjk.yoram.view.fragment.main.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.sjk.yoram.databinding.FragMyPrefAdminBannerBinding
import com.sjk.yoram.model.ui.adapter.AdminBannerListAdapter
import com.sjk.yoram.model.ui.decorator.AdminBannerTouchHelper
import com.sjk.yoram.viewmodel.AdminBannerViewModel
import com.sjk.yoram.viewmodel.PrefViewModel

class AdminBannerFragment: Fragment() {
    private lateinit var binding: FragMyPrefAdminBannerBinding
    private lateinit var prefViewModel: PrefViewModel
    private lateinit var viewModel: AdminBannerViewModel

    private lateinit var adapter: AdminBannerListAdapter
    private lateinit var itemTouchHelper: AdminBannerTouchHelper
    private lateinit var helper: ItemTouchHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        prefViewModel = ViewModelProvider(requireActivity())[PrefViewModel::class.java]
        viewModel = ViewModelProvider(this, AdminBannerViewModel.Factory(requireActivity().application))[AdminBannerViewModel::class.java]
        binding = FragMyPrefAdminBannerBinding.inflate(layoutInflater)
        binding.prefVM = prefViewModel
        binding.vm = viewModel
        binding.lifecycleOwner = this

        adapter = AdminBannerListAdapter()
        itemTouchHelper = AdminBannerTouchHelper(adapter)
        helper = ItemTouchHelper(itemTouchHelper)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.setDragListener(object: AdminBannerListAdapter.ItemDragListener {
            override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                helper.startDrag(viewHolder)
            }
        })
        binding.fragMyPrefAdminBannerBodyRecycler.adapter = adapter
        helper.attachToRecyclerView(binding.fragMyPrefAdminBannerBodyRecycler)
    }

}