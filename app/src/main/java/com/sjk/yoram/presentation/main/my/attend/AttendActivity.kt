package com.sjk.yoram.presentation.main.my.attend

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.sjk.yoram.R
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*

class AttendActivity: AppCompatActivity() {
    private val binding by lazy { com.sjk.yoram.databinding.ActivityMyAttendBinding.inflate(layoutInflater) }
    private lateinit var viewModel: AttendViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, AttendViewModel.Factory(application))[AttendViewModel::class.java]
        binding.vm = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)
        supportActionBar?.hide()
        initView()

        viewModel.backEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                setResult(RESULT_OK)
                finish()
            }
        }

        viewModel.refreshEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                val rotate = AnimationUtils.loadAnimation(applicationContext, R.anim.anim_rotate)
                binding.myAttendRefresh.startAnimation(rotate)
            }
        }
    }

    private fun initView() {
        binding.myAttendCalendar.monthHeaderBinder = object: MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View): MonthViewContainer = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
            }
        }

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(12)
        val lastMonth = currentMonth.plusMonths(0)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek

        binding.myAttendCalendar.setup(firstMonth, lastMonth, firstDayOfWeek)
        binding.myAttendCalendar.scrollToMonth(currentMonth)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}