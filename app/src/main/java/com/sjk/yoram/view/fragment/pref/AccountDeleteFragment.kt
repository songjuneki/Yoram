package com.sjk.yoram.view.fragment.pref

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragMyPrefAccountDeleteBinding
import com.sjk.yoram.viewmodel.AccountDeleteViewModel
import com.sjk.yoram.viewmodel.PrefViewModel

class AccountDeleteFragment: Fragment() {
    private lateinit var binding: FragMyPrefAccountDeleteBinding
    private lateinit var viewModel: AccountDeleteViewModel
    private lateinit var prefViewModel: PrefViewModel

    private lateinit var accountDeleteFragmentManager: FragmentManager
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragMyPrefAccountDeleteBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, AccountDeleteViewModel.Factory(requireActivity().application))[AccountDeleteViewModel::class.java]
        prefViewModel = ViewModelProvider(requireActivity())[PrefViewModel::class.java]
        binding.vm = viewModel
        binding.prefVM = prefViewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        accountDeleteFragmentManager = childFragmentManager
        navHostFragment = accountDeleteFragmentManager.findFragmentById(R.id.frag_my_pref_account_delete_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.exitEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigateUp()
            }
        }

        viewModel.backEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                navController.navigateUp()
            }
        }

        viewModel.changeFragmentEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                navController.navigate(it)
            }
        }

        viewModel.toastEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }
}