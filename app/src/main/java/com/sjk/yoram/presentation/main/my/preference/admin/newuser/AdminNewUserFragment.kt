package com.sjk.yoram.presentation.main.my.preference.admin.newuser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sjk.yoram.databinding.FragMyPrefAdminNewUserBinding
import com.sjk.yoram.presentation.main.my.preference.PrefViewModel

class AdminNewUserFragment: Fragment() {
    private lateinit var binding: FragMyPrefAdminNewUserBinding
    private lateinit var prefViewModel: PrefViewModel
    private lateinit var viewModel: AdminNewUserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragMyPrefAdminNewUserBinding.inflate(layoutInflater)
        prefViewModel = ViewModelProvider(requireActivity())[PrefViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity())[AdminNewUserViewModel::class.java]

        binding.prefVM = prefViewModel
        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.toastEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }
}