package com.sjk.yoram.Controller.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragBoardBinding
import com.sjk.yoram.databinding.FragHomeBinding

class BoardFragment: Fragment() {
    // private val binding by lazy { FragBoardBinding.inflate(layoutInflater) }
    private lateinit var binding: FragBoardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val title = requireArguments().getString("title")
        //Log.d("jk", "${title} 오픈")
        this.binding = DataBindingUtil.inflate(inflater, R.layout.frag_board, container, false)
        return this.binding.root
    }

    companion object {
        fun newInstance(title:String) = BoardFragment().apply {
            arguments = Bundle().apply {
                putString("title", title)
            }
        }
    }

}