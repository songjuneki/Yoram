package com.sjk.yoram.view.activity.main.my

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.sjk.yoram.R
import com.sjk.yoram.databinding.ActivityMySettingBinding
import com.sjk.yoram.viewmodel.SettingViewModel

class SettingActivity: AppCompatActivity() {
    private val binding by lazy { ActivityMySettingBinding.inflate(layoutInflater) }
    private lateinit var viewModel: SettingViewModel
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, SettingViewModel.Factory(application))[SettingViewModel::class.java]
        binding.vm = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)
        supportActionBar?.hide()

        navHostFragment = supportFragmentManager.findFragmentById(R.id.my_setting_frame) as NavHostFragment
        navController = navHostFragment.navController

        viewModel.naviEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                navController.navigate(it)
            }
        }

        viewModel.toastEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}