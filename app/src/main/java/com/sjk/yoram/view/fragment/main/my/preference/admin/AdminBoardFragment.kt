package com.sjk.yoram.view.fragment.main.my.preference.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragMyPrefAdminBoardBinding
import com.sjk.yoram.model.ui.adapter.AdminBoardListAdapter
import com.sjk.yoram.model.ui.adapter.AdminReserveBoardListAdapter
import com.sjk.yoram.model.ui.listener.AdminBoardItemTouchCallback
import com.sjk.yoram.viewmodel.AdminBoardUiState
import com.sjk.yoram.viewmodel.AdminBoardViewModel
import com.sjk.yoram.viewmodel.PrefViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminBoardFragment: Fragment() {
    private val binding by lazy { FragMyPrefAdminBoardBinding.inflate(layoutInflater) }
    private val viewModel: AdminBoardViewModel by activityViewModels()
    private val prefViewModel: PrefViewModel by activityViewModels()

    private lateinit var topListAdapter: AdminReserveBoardListAdapter
    private lateinit var bottomListAdapter: AdminBoardListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.vm = viewModel

        initList()

        binding.bindState(
            viewModel,
            viewModel.uiState,
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragMyPrefAdminBoardBackBtn.setOnClickListener {
            prefViewModel.backBtn()
        }

        binding.fragMyPrefAdminBoardDone.setOnClickListener {
            findNavController().navigate(R.id.action_adminBoardFragment_to_prefApplyDialogFragment)
        }
    }

    private fun initList() {
        viewModel.initialListData()

        topListAdapter = AdminReserveBoardListAdapter(
            onClick = viewModel::moveToHide,
            onMove = viewModel::onReorderInReserveList
        )
        val topItemTouchHelper = ItemTouchHelper(AdminBoardItemTouchCallback(topListAdapter))

        bottomListAdapter = AdminBoardListAdapter(
            onClick = viewModel::moveToReserve,
            onMove = viewModel::onReorderInHideList
        )
        val bottomItemTouchHelper = ItemTouchHelper(AdminBoardItemTouchCallback(bottomListAdapter))

        binding.fragMyPrefAdminBoardReserveRecycler.adapter = topListAdapter
        topItemTouchHelper.attachToRecyclerView(binding.fragMyPrefAdminBoardReserveRecycler)

        binding.fragMyPrefAdminBoardRecycler.adapter = bottomListAdapter
        bottomItemTouchHelper.attachToRecyclerView(binding.fragMyPrefAdminBoardRecycler)
    }


    private fun FragMyPrefAdminBoardBinding.bindState(
        viewModel: AdminBoardViewModel,
        uiState: StateFlow<AdminBoardUiState>,
    ) {
        bindList(uiState)
        bindButton(uiState)
        bindMessage(viewModel, uiState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    uiState
                        .map { it.isLoaded }
                        .collectLatest {
                            // 로딩 뷰 바인딩
                            fragMyPrefAdminBoardLoading.isVisible = !it
                            root.isEnabled = it
                        }
                }

                launch {
                    uiState
                        .map { it.isExit }
                        .filter { it }
                        .collectLatest {
                            findNavController().popBackStack(R.id.prefFragment, false)
                        }
                }
            }
        }
    }

    private fun FragMyPrefAdminBoardBinding.bindList(uiState: StateFlow<AdminBoardUiState>) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    uiState
                        .map { it.reserveList }
                        .distinctUntilChanged()
                        .collectLatest(topListAdapter::submitList)
                }

                launch {
                    uiState
                        .map { it.hideList }
                        .distinctUntilChanged()
                        .collectLatest(bottomListAdapter::submitList)
                }
            }
        }
    }

    private fun FragMyPrefAdminBoardBinding.bindButton(uiState: StateFlow<AdminBoardUiState>) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    uiState
                        .map { it.isChanged }
                        .collectLatest {
                            val backgroundColor = resources.getColor(
                                if (it) R.color.xd_light_dot_indicator_enabled
                                else R.color.xd_light_text_hint,
                                null)

                            fragMyPrefAdminBoardDone.setBackgroundColor(backgroundColor)
                            fragMyPrefAdminBoardDone.isEnabled = it
                        }
                }
            }
        }
    }

    private fun FragMyPrefAdminBoardBinding.bindMessage(
        viewModel: AdminBoardViewModel,
        uiState: StateFlow<AdminBoardUiState>
    ) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                uiState
                    .mapNotNull { it.userMessage }
                    .collectLatest {
                        Snackbar.make(root, it, Snackbar.LENGTH_LONG)
                            .setAction("재시도") { _ -> viewModel.applyReserveBoardCategoryList() }
                            .show()
                        viewModel.messageShown()
                    }
            }
        }
    }
}