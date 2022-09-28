package com.sjk.yoram.view.activity.main.my

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.sjk.yoram.R
import com.sjk.yoram.databinding.ActivityMyPrefBinding
import com.sjk.yoram.viewmodel.FragPrivacyViewModel
import com.sjk.yoram.viewmodel.PrefViewModel

class PreferenceActivity: AppCompatActivity() {
    private val binding by lazy { ActivityMyPrefBinding.inflate(layoutInflater) }
    private lateinit var viewModel: PrefViewModel
    private lateinit var privacyViewModel: FragPrivacyViewModel

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, PrefViewModel.Factory(application))[PrefViewModel::class.java]
        privacyViewModel = ViewModelProvider(this, FragPrivacyViewModel.Factory(application))[FragPrivacyViewModel::class.java]
        binding.vm = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)
        supportActionBar?.hide()

        navHostFragment = supportFragmentManager.findFragmentById(R.id.my_pref_frame) as NavHostFragment
        navController = navHostFragment.navController

        viewModel.naviEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                navController.navigate(it)
            }
        }

        viewModel.backEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                if (navHostFragment.childFragmentManager.backStackEntryCount == 0) {
                    setResult(RESULT_OK)
                    finish()
                } else {
                    onSupportNavigateUp()
                }
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

    override fun onSupportNavigateUp(): Boolean {
        return when (navController.currentDestination?.id) {
            R.id.prefFragment -> { finish(); true }
            R.id.prefPrivacyFragment -> {
                if (privacyViewModel.ppIsChanged()) {
                    navController.navigate(R.id.action_prefPrivacyFragment_to_prefApplyDialogFragment)
                    true
                } else {
                    navController.navigateUp()
                }
            }
            R.id.prefApplyDialogFragment -> {
                privacyViewModel.loadPP()
                navController.popBackStack(R.id.prefFragment, false)
            }
            else -> { navController.navigateUp() }
        }
    }

    override fun onBackPressed() {
        onSupportNavigateUp()
    }
}