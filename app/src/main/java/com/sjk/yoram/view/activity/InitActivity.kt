package com.sjk.yoram.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.sjk.yoram.R
import com.sjk.yoram.databinding.ActivityInitBinding
import com.sjk.yoram.view.fragment.init.InitHomeFragment
import com.sjk.yoram.view.fragment.init.InitLoginFragment
import com.sjk.yoram.viewmodel.InitViewModel

class InitActivity: AppCompatActivity() {
    private val binding by lazy { ActivityInitBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this, InitViewModel.Factory(application))[InitViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.vm = viewModel
//        binding.lifecycleOwner = this

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<InitHomeFragment>(R.id.init_frag_view, "init_home")
            }
        }

        viewModel.btnEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let { btnId ->
                when (btnId) {
                    R.id.init_login_gologin -> {supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        add<InitLoginFragment>(R.id.init_frag_view, "init_login")
                        addToBackStack("init_login")
                    }}
                    R.id.init_login_signup_btn -> {}
                    R.id.init_login_anonymous_btn -> {
                        val main = Intent(this, MainActivity::class.java)
                        main.putExtra("loginID", -1)
                        startActivity(main)
//                        finish()
                    }
                    R.id.init_login_need_help -> {}
                }
            }
        }
    }
}