package com.sjk.yoram.model

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class YoramFragment<T: ViewDataBinding>(@LayoutRes val layoutId: Int): Fragment() {
    private lateinit var mBinding: T
    protected val binding get() = mBinding

    var isInitialized = false
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::mBinding.isInitialized)
            mBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this.viewLifecycleOwner

        if (!isInitialized) {
            init()
            isInitialized = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    abstract fun init()
}