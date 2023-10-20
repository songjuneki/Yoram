package com.sjk.yoram.presentation.main.identification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.sjk.yoram.presentation.main.MainViewModel
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragIdBinding

class IDFragment: Fragment() {
    private lateinit var binding: FragIdBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: FragIDViewModel by navGraphViewModels(R.id.navi_id) { FragIDViewModel.Factory(requireActivity().application) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_id, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.mainVM = mainViewModel
        binding.vm = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.backEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { findNavController().popBackStack(R.id.iDFragment, false) }
        }
        viewModel.navEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(it)
            }
        }

        mainViewModel.onBottomMenuReselected.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                viewModel.hiddenFeature()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.countStop()
    }

}