package com.sjk.yoram.Controller.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragMyBinding

class MyFragment: Fragment() {
    // private val binding by lazy { FragMyBinding.inflate(layoutInflater) }
    private lateinit var binding: FragMyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val title = requireArguments().getString("title")
        //Log.d("jk", "${title} 오픈")
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_my, container, false)
        return binding.root
    }

    companion object {
        fun newInstance(title:String) = MyFragment().apply {
            arguments = Bundle().apply {
                putString("title", title)
            }
        }
    }

}