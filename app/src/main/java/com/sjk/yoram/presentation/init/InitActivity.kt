package com.sjk.yoram.presentation.init

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.sjk.yoram.R
import com.sjk.yoram.databinding.ActivityInitBinding
import com.sjk.yoram.presentation.main.MainActivity

class InitActivity: AppCompatActivity() {
    private val binding by lazy { ActivityInitBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this, InitViewModel.Factory(application))[InitViewModel::class.java] }
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.vm = viewModel

        navHostFragment = supportFragmentManager.findFragmentById(R.id.init_frag_view) as NavHostFragment
        navController = navHostFragment.navController

        viewModel.backBtnEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let { navController.popBackStack() }
        }

        viewModel.naviAction.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                navController.navigate(it)
            }
        }

        viewModel.progressEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                binding.initLoadingLayout.visibility = if(it) View.VISIBLE else View.GONE
            }
        }

        viewModel.msgEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.anonymousLoginEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                val main = Intent(this, MainActivity::class.java)
                main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                main.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                finish()
                startActivity(main)
                overridePendingTransition(0, 0)
            }
        }

    }

}