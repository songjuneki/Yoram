package com.sjk.yoram.presentation.main.my.preference

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragMyPrefBinding

class PreferenceFragment: Fragment() {
    private lateinit var binding: FragMyPrefBinding
    private lateinit var viewModel: PrefViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_my_pref, container, false)
        viewModel = ViewModelProvider(requireActivity())[PrefViewModel::class.java]
        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        binding.fragMyPrefScroll.scrollY = savedInstanceState?.getInt("scroll") ?: 0
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("scroll", binding.fragMyPrefScroll.scrollY)
    }
}