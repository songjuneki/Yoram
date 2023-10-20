package com.sjk.yoram.presentation.main.my.attend

import android.view.View
import com.kizitonwose.calendarview.ui.ViewContainer
import com.sjk.yoram.databinding.CalendarDayLayoutBinding

class DayViewContainer(view: View): ViewContainer(view) {
    val binding = CalendarDayLayoutBinding.bind(view)
    val textView = binding.calendarDayText
    val checker = binding.calendarDayCheck
}