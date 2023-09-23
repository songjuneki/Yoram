package com.sjk.yoram.view.activity.main

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.sjk.yoram.R
import com.sjk.yoram.databinding.ActivityMainBinding
import com.sjk.yoram.view.activity.InitActivity
import com.sjk.yoram.viewmodel.*

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory(application) }

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private var backPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.vm = viewModel
        binding.lifecycleOwner = this

        supportActionBar?.hide()

        if (savedInstanceState == null)
            initNavigation()
        initLiveDataObserve()
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

    private fun initNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_frame) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(setOf(R.id.navi_home, R.id.navi_dptment, R.id.navi_id, R.id.navi_board, R.id.navi_my))
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavi.setupWithNavController(navController)
    }

    private fun initLiveDataObserve() {
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

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        initNavigation()
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }
}
