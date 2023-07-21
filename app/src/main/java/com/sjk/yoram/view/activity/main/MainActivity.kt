package com.sjk.yoram.view.activity.main

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sjk.yoram.R
import com.sjk.yoram.databinding.ActivityMainBinding
import com.sjk.yoram.model.FragmentType
import com.sjk.yoram.view.activity.InitActivity
import com.sjk.yoram.view.fragment.main.*
import com.sjk.yoram.viewmodel.*
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory(application) }

    private val homeFragment = HomeFragment()
    private val departmentFragment = DepartmentFragment()
    private val idFragment = IdFragment()
    private val boardFragment = BoardFragment()
    private val myFragment = MyFragment()

    private val fragmentAdapter by lazy { FragmentAdapter(this, listOf(homeFragment, departmentFragment, idFragment, boardFragment, myFragment)) }

    private var backPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.vm = viewModel
        binding.lifecycleOwner = this

        binding.mainFrame.isUserInputEnabled = false
        binding.mainFrame.offscreenPageLimit = 2
        binding.mainFrame.setPageTransformer { page, position ->
            if (position <= -1.0f || position >= 1.0f) {
                page.translationX = page.width * position
                page.alpha = 0.0f
            } else if (position == 0.0f) {
                page.translationX = page.width * position
                page.alpha = 1.0f
            } else {
                page.translationX = page.width * -position
                page.alpha = 1.0f - abs(position)
            }
        }
        binding.mainFrame.adapter = fragmentAdapter

        binding.bottomNavi.setOnItemSelectedListener {
            val page = when (it.itemId) {
                R.id.navi_home -> 0
                R.id.navi_dptment -> 1
                R.id.navi_id -> 2
                R.id.navi_board -> 3
                R.id.navi_my -> 4
                else -> 0
            }
            binding.mainFrame.setCurrentItem(page, false)

            val type = when (it.itemId) {
                R.id.navi_home -> FragmentType.Fragment_HOME
                R.id.navi_dptment -> FragmentType.Fragment_DPTMENT
                R.id.navi_id -> FragmentType.Fragment_ID
                R.id.navi_board -> FragmentType.Fragment_BOARD
                R.id.navi_my -> FragmentType.Fragment_MY
                else -> FragmentType.Fragment_HOME
            }
            viewModel.setCurrentFragment(type)
            true
        }
        supportActionBar?.hide()


        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (binding.bottomNavi.selectedItemId) {
                    R.id.navi_home -> {
                        if (System.currentTimeMillis() - backPressedTime <= 2000L)
                            ActivityCompat.finishAffinity(this@MainActivity)
                        else {
                            backPressedTime = System.currentTimeMillis()
                            Toast.makeText(this@MainActivity, "앱을 종료하려면 한번 더 눌러주세요", Toast.LENGTH_SHORT).show()
                        }
                    }
                    R.id.navi_dptment -> {
                        val currentDestination = departmentFragment.getNavController().currentDestination?.id
                        if (currentDestination == departmentFragment.getNavController().graph.startDestinationId)
                            viewModel.fragMoveHome()
                        else
                            departmentFragment.getNavController().navigateUp()
                    }
                    R.id.navi_id -> {
                        val currentDestination = idFragment.getNavController().currentDestination?.id
                        if (currentDestination == idFragment.getNavController().graph.startDestinationId)
                            viewModel.fragMoveHome()
                        else
                            idFragment.getNavController().navigateUp()
                    }
                    R.id.navi_board -> {
                        val currentDestination = boardFragment.getNavController().currentDestination?.id
                        if (currentDestination == boardFragment.getNavController().graph.startDestinationId)
                            viewModel.fragMoveHome()
                        else
                            boardFragment.getNavController().navigateUp()
                    }
                    R.id.navi_my -> {
                        val currentDestination = myFragment.getNavController().currentDestination?.id
                        if (currentDestination == myFragment.getNavController().graph.startDestinationId)
                            viewModel.fragMoveHome()
                        else
                            myFragment.getNavController().navigateUp()
                    }
                }
            }
        })

        viewModel.loginEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                val initIntent = Intent(this, InitActivity::class.java)
                initIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                initIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                finish()
                startActivity(initIntent)
                overridePendingTransition(0, 0)
            }
        }

        viewModel.moveFragmentEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                binding.bottomNavi.selectedItemId = it
            }
        }

        viewModel.privacyAgreeEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
//                navController.navigate(R.id.action_global_main_to_privacyAgreeFragment)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) { //거부
                Toast.makeText(this@MainActivity, "카메라 권한 거부", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadLoginData()
        viewModel.loadGiveAmount()
        viewModel.checkRuleAgreeExpire()
    }

    inner class FragmentAdapter(activity: AppCompatActivity, private val fragments: List<Fragment>) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
    }
}
