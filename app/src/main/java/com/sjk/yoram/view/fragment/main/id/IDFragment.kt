package com.sjk.yoram.view.fragment.main.id

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.sjk.yoram.viewmodel.MainViewModel
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragIdBinding
import com.sjk.yoram.model.YoramFragment
import com.sjk.yoram.viewmodel.FragIDViewModel

class IDFragment: YoramFragment<FragIdBinding>(R.layout.frag_id) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: FragIDViewModel by navGraphViewModels(R.id.navi_id) { FragIDViewModel.Factory(requireActivity().application) }


    override fun init() {
        binding.mainVM = mainViewModel
        binding.vm = viewModel
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
    }

    override fun onPause() {
        super.onPause()
        viewModel.countStop()
    }

}