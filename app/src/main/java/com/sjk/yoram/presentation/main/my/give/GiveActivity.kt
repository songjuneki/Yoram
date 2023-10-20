package com.sjk.yoram.presentation.main.my.give

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sjk.yoram.R
import com.sjk.yoram.databinding.ActivityMyGiveBinding
import com.sjk.yoram.presentation.common.SimpleItemDivider

class GiveActivity: AppCompatActivity() {
    private val binding by lazy { ActivityMyGiveBinding.inflate(layoutInflater) }
    private lateinit var viewModel: GiveViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, GiveViewModel.Factory(application))[GiveViewModel::class.java]
        binding.vm = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.myGiveRecycler.addItemDecoration(SimpleItemDivider(applicationContext, LinearLayoutManager.VERTICAL))

        viewModel.bakcEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                setResult(RESULT_OK)
                finish()
            }
        }

        viewModel.refreshEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                val rotate = AnimationUtils.loadAnimation(applicationContext, R.anim.anim_rotate)
                binding.myGiveRefresh.startAnimation(rotate)
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}