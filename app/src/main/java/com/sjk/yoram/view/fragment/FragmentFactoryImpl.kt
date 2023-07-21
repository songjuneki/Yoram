package com.sjk.yoram.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.sjk.yoram.view.fragment.main.board.BoardFragment
import com.sjk.yoram.view.fragment.main.department.DptmentFragment
import com.sjk.yoram.view.fragment.main.home.HomeFragment
import com.sjk.yoram.view.fragment.main.id.IDFragment
import com.sjk.yoram.view.fragment.main.my.MyFragment

class FragmentFactoryImpl(private val data: Int): FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className) {
            HomeFragment::class.java.name -> HomeFragment()
            DptmentFragment::class.java.name -> DptmentFragment()
            IDFragment::class.java.name -> {
                IDFragment().apply {
                    arguments = Bundle().apply {
                        putInt("data", data)
                    }
                }
            }
            BoardFragment::class.java.name -> BoardFragment()
            MyFragment::class.java.name -> MyFragment()
            else -> super.instantiate(classLoader, className)
        }
    }
}