package com.sjk.yoram.view.activity.main.my

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.sjk.yoram.R
import com.sjk.yoram.databinding.ActivityMyEditBinding
import com.sjk.yoram.view.fragment.main.my.EditAvatarDialog
import com.sjk.yoram.viewmodel.EditViewModel

class EditActivity: AppCompatActivity() {
    private val binding by lazy { ActivityMyEditBinding.inflate(layoutInflater) }
    private lateinit var viewModel: EditViewModel
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, EditViewModel.Factory(application))[EditViewModel::class.java]
        binding.vm = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)
        supportActionBar?.hide()

        navHostFragment = supportFragmentManager.findFragmentById(R.id.my_edit_frame) as NavHostFragment
        navController = navHostFragment.navController

        viewModel.exitEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                setResult(RESULT_OK)
                finish()
            }
        }

        viewModel.backEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                navController.popBackStack()
            }
        }

        viewModel.navEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                navController.navigate(it)
            }
        }

        viewModel.loadingEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                binding.myEditLoading.visibility = View.VISIBLE
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


    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }



}