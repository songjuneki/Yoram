package com.sjk.yoram.view.activity.main.my

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sjk.yoram.R
import com.sjk.yoram.databinding.ActivityMyGiveBinding
import com.sjk.yoram.model.ui.decorator.SimpleItemDivider
import com.sjk.yoram.viewmodel.GiveViewModel

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
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}