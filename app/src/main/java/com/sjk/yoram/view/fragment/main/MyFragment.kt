package com.sjk.yoram.view.fragment.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.load
import com.sjk.yoram.R
import com.sjk.yoram.viewmodel.MainViewModel
import com.sjk.yoram.databinding.FragMyBinding
import com.sjk.yoram.view.activity.main.my.AttendActivity
import com.sjk.yoram.view.activity.main.my.EditActivity
import com.sjk.yoram.view.activity.main.my.GiveActivity
import com.sjk.yoram.viewmodel.FragMyViewModel

class MyFragment: Fragment() {
    private lateinit var binding: FragMyBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var viewModel: FragMyViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_my, container, false)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity())[FragMyViewModel::class.java]
        binding.mainVM = mainViewModel
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.editEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                val intent = Intent(requireContext(), EditActivity::class.java)
                myEditResult.launch(intent)
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

        viewModel.giveEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                val intent = Intent(requireContext(), GiveActivity::class.java)
                myGiveResult.launch(intent)
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

        viewModel.attendEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                val intent = Intent(requireContext(), AttendActivity::class.java)
                startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
    }

    private val myEditResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            mainViewModel.loadLoginData()
        }
    }

    private val myGiveResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            mainViewModel.loadGiveAmount()
        }
    }
}