package com.sjk.yoram.presentation.main.department

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sjk.yoram.presentation.main.MainViewModel
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragDptmentBinding
import kotlinx.coroutines.*

class DptmentFragment: Fragment() {
    private lateinit var binding: FragDptmentBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: FragDptmentViewModel by viewModels { FragDptmentViewModel.Factory(requireActivity().application) }

    private var showShimmer: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_dptment, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.vm = viewModel
        binding.fragDptmentHeaderSpinner.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.dptmentFragment)
                viewModel.hideSearchbar()
        }
        viewModel.userDetailEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                if (findNavController().currentDestination?.id == R.id.dptmentFragment)
                    findNavController().navigate(R.id.action_dptFragment_to_userInfoDialog)
            }
        }

        viewModel.searchEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                binding.fragDptmentHeaderSearchbar.requestFocus()
            }
        }

        viewModel.isLoadingDptServer.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                if(it)
                    showShimmer = makeShowShimmerJob()
                else
                    hideShimmer()
            }
        }

        mainViewModel.goDptSearchEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                binding.fragDptmentHeaderSearch.callOnClick()
            }
        }
    }


    override fun onPause() {
        super.onPause()
        viewModel.departmentNodeList.value = viewModel.nodeListAdapter.saveDepartmentNodeList().toMutableList()
    }


    private fun makeShowShimmerJob() = lifecycleScope.launch {
        binding.fragDptmentRecycler.visibility = View.INVISIBLE
        binding.fragDptmentShimmer.startShimmer()
        binding.fragDptmentShimmer.visibility = View.VISIBLE
        delay(1500)
    }

    private fun hideShimmer() {
        showShimmer?.let {
            if(it.isActive) {
                showShimmer?.invokeOnCompletion {
                    binding.fragDptmentShimmer.stopShimmer()
                    binding.fragDptmentShimmer.visibility = View.GONE
                    binding.fragDptmentRecycler.visibility = View.VISIBLE
                    showShimmer = null
                }
            } else {
                binding.fragDptmentShimmer.stopShimmer()
                binding.fragDptmentShimmer.visibility = View.GONE
                binding.fragDptmentRecycler.visibility = View.VISIBLE
                showShimmer = null
            }
        }
    }
}