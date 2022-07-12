package com.sjk.yoram.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sjk.yoram.databinding.ActivityPolicyDetailBinding

class PolicyActivity: AppCompatActivity() {
    private val binding by lazy { ActivityPolicyDetailBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.close.setOnClickListener {
            finish()
        }
    }
}