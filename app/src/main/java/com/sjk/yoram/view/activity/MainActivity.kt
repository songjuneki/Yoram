package com.sjk.yoram.view.activity

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.sjk.yoram.R
import com.sjk.yoram.databinding.ActivityMainBinding
import com.sjk.yoram.viewmodel.FragDptmentViewModel
import com.sjk.yoram.viewmodel.MainViewModel
import com.sjk.yoram.viewmodel.FragHomeViewModel

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var viewModel: MainViewModel
    private lateinit var homeFragViewModel: FragHomeViewModel
    private lateinit var dptFragViewModel: FragDptmentViewModel

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, MainViewModel.Factory(application)).get(MainViewModel::class.java)
        homeFragViewModel = ViewModelProvider(this, FragHomeViewModel.Factory(application)).get(FragHomeViewModel::class.java)
        dptFragViewModel = ViewModelProvider(this, FragDptmentViewModel.Factory(application))[FragDptmentViewModel::class.java]

        binding.vm = viewModel

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainFrame) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavi.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(setOf(R.id.navi_home, R.id.navi_dptment, R.id.navi_id, R.id.navi_board, R.id.navi_my))
        supportActionBar?.hide()
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
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
}