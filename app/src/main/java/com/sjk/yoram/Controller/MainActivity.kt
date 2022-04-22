package com.sjk.yoram.Controller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sjk.yoram.Controller.Fragment.*
import com.sjk.yoram.Model.FragmentType
import com.sjk.yoram.R
import com.sjk.yoram.databinding.ActivityMainBinding
import com.sjk.yoram.MainVM
import com.sjk.yoram.viewmodel.FragDptmentViewModel
import com.sjk.yoram.viewmodel.FragHomeViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainVM by viewModels()
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }       // viewBinding
    private lateinit var dptFragViewModel: FragDptmentViewModel
    private lateinit var homeFragViewModel: FragHomeViewModel

    private lateinit var navi: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)      // viewBinding
        binding.vm = viewModel
        homeFragViewModel = ViewModelProvider(this).get(FragHomeViewModel::class.java)
        dptFragViewModel = ViewModelProvider(this).get(FragDptmentViewModel::class.java)

        viewModel.currentFragmentType.observe(this) {
            changeFragment(it)
            when (it) {
                FragmentType.Fragment_HOME -> navi.selectedItemId = R.id.navi_home
                FragmentType.Fragment_DPTMENT -> navi.selectedItemId = R.id.navi_dptment
                FragmentType.Fragment_ID -> navi.selectedItemId = R.id.navi_id
                FragmentType.Fragment_BOARD -> navi.selectedItemId = R.id.navi_board
                FragmentType.Fragment_MY -> navi.selectedItemId = R.id.navi_my
            }
        }

        dptFragViewModel.loadAllDepartmentsByDpt()

        this.navi = binding.bottomNavi
        changeFragment(FragmentType.Fragment_HOME)
    }

    private fun changeFragment(fragmentType: FragmentType) {
        val transaction = supportFragmentManager.beginTransaction()
        var targetFragment = supportFragmentManager.findFragmentByTag(fragmentType.tag)

        if (targetFragment == null) {
            targetFragment = getFragment(fragmentType)
            transaction.add(R.id.mainFrame, targetFragment, fragmentType.tag)
        }
        transaction.show(targetFragment)

        FragmentType.values()
            .filterNot { it == fragmentType }
            .forEach { type ->
                supportFragmentManager.findFragmentByTag(type.tag)?.let {
                    transaction.hide(it)
                }
            }
        transaction.commitAllowingStateLoss()
    }


    private fun getFragment(fragmentType: FragmentType): Fragment {
        return when(fragmentType) {
            FragmentType.Fragment_HOME -> HomeFragment()
            FragmentType.Fragment_DPTMENT -> DptmentFragment()
            FragmentType.Fragment_ID -> IDFragment()
            FragmentType.Fragment_BOARD -> BoardFragment()
            FragmentType.Fragment_MY -> MyFragment()
        }
    }

}