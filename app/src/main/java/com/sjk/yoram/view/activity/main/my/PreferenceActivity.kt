package com.sjk.yoram.view.activity.main.my

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.sjk.yoram.R
import com.sjk.yoram.databinding.ActivityMyPrefBinding
import com.sjk.yoram.view.fragment.main.my.*
import com.sjk.yoram.viewmodel.*

class PreferenceActivity: AppCompatActivity() {
    private val binding by lazy { ActivityMyPrefBinding.inflate(layoutInflater) }
    private lateinit var viewModel: PrefViewModel
    private lateinit var privacyViewModel: FragPrivacyViewModel
    private lateinit var adminBannerViewModel: AdminBannerViewModel
    private lateinit var adminDepartmentViewModel: AdminDepartmentViewModel
    private lateinit var adminPositionViewModel: AdminPositionViewModel
    private lateinit var adminNewUserViewModel: AdminNewUserViewModel

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, PrefViewModel.Factory(application))[PrefViewModel::class.java]
        privacyViewModel = ViewModelProvider(this, FragPrivacyViewModel.Factory(application))[FragPrivacyViewModel::class.java]
        adminBannerViewModel = ViewModelProvider(this, AdminBannerViewModel.Factory(application))[AdminBannerViewModel::class.java]
        adminDepartmentViewModel = ViewModelProvider(this, AdminDepartmentViewModel.Factory(application))[AdminDepartmentViewModel::class.java]
        adminPositionViewModel = ViewModelProvider(this, AdminPositionViewModel.Factory(application))[AdminPositionViewModel::class.java]
        adminNewUserViewModel = ViewModelProvider(this, AdminNewUserViewModel.Factory(application))[AdminNewUserViewModel::class.java]

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

        viewModel.applyEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                val screenFragment = navHostFragment.childFragmentManager.fragments[0]
                when (screenFragment) {
                    is PrefPrivacyFragment -> privacyViewModel.changedValueApply()
                    is AdminBannerFragment -> adminBannerViewModel.changedValueApply()
                    is AdminWorshipFragment -> viewModel.changedWorshipTypeApply()
                    is AdminGiveTypeFragment -> viewModel.changedGiveTypeApply()
                    is AdminDepartmentFragment -> adminDepartmentViewModel.changedDepartmentListApply()
                    is AdminPositionFragment -> adminPositionViewModel.changedPositionListApply()
                }
            }
        }

        viewModel.applyCancelEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                navController.navigate(R.id.action_prefApplyDialogFragment_to_prefFragment)
            }
        }

        viewModel.toastEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.logoutEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                setResult(RESULT_LOGOUT)
                finish()
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
                } else navController.navigateUp()
            }
            R.id.prefApplyDialogFragment -> {
//                privacyViewModel.loadPP()
//                navController.popBackStack(R.id.prefFragment, false)
                navController.navigateUp()
            }
            R.id.adminBannerFragment -> {
                if (adminBannerViewModel.bannerIsChanged()) {
                    navController.navigate(R.id.action_adminBannerFragment_to_prefApplyDialogFragment)
                    true
                } else navController.navigateUp()
            }
            R.id.adminWorshipFragment -> {
                if (viewModel.worshipChanged.value!!) {
                    navController.navigate(R.id.action_adminWorshipFragment_to_prefApplyDialogFragment)
                    true
                } else navController.navigateUp()
            }
            R.id.adminGiveTypeFragment -> {
                if (viewModel.giveTypeChanged.value!!) {
                    navController.navigate(R.id.action_adminGiveTypeFragment_to_prefApplyDialogFragment)
                    true
                } else navController.navigateUp()
            }
            R.id.adminDepartmentFragment -> {
                if (adminDepartmentViewModel.isChanged.value!!) {
                    navController.navigate(R.id.action_adminDepartmentFragment_to_prefApplyDialogFragment)
                    true
                } else navController.navigateUp()
            }
            R.id.adminPositionFragment -> {
                if (adminPositionViewModel.isChanged.value!!) {
                    navController.navigate(R.id.action_adminPositionFragment_to_prefApplyDialogFragment)
                    true
                } else navController.navigateUp()
            }
            else -> navController.navigateUp()
        }
    }

    override fun onBackPressed() {
        onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1000) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "카메라 권한 거부", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == 1100) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "파일 읽기 권한 거부", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == 1200) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "파일 쓰기 권한 거부", Toast.LENGTH_SHORT).show()
            }
        }

    }

    companion object {
        const val RESULT_LOGOUT = 19980105
    }
}