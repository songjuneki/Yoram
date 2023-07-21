package com.sjk.yoram.view.fragment.main.my.preference.delete

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sjk.yoram.databinding.FragMyPrefAccountDeleteFinishBinding
import com.sjk.yoram.view.activity.SplashActivity
import com.sjk.yoram.viewmodel.AccountDeleteViewModel
import kotlin.system.exitProcess

class AccountDeleteFinishFragment: Fragment() {
    private lateinit var binding: FragMyPrefAccountDeleteFinishBinding
    private lateinit var viewModel: AccountDeleteViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragMyPrefAccountDeleteFinishBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireParentFragment().requireParentFragment())[AccountDeleteViewModel::class.java]
        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.shutdownEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                ActivityCompat.finishAffinity(requireActivity())
                val intent = Intent(requireContext(), SplashActivity::class.java)
                startActivity(intent)
                exitProcess(0)
            }
        }
    }
}