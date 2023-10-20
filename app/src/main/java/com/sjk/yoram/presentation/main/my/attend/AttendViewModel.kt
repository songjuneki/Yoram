package com.sjk.yoram.presentation.main.my.attend

import android.app.Application
import androidx.lifecycle.*
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.ui.MonthScrollListener
import com.sjk.yoram.R
import com.sjk.yoram.util.Event
import com.sjk.yoram.data.repository.UserRepository
import kotlinx.coroutines.async
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class AttendViewModel(private val userRepository: UserRepository): ViewModel() {
    val name = MutableLiveData<String>()
    val pos = MutableLiveData<String>()

    private val _total = MutableLiveData("0")
    val total: LiveData<String>
        get() = _total

    private val _dateStr = MutableLiveData<String>()
    val dateStr: LiveData<String>
        get() = _dateStr

    val currentMonth = MutableLiveData<YearMonth>()

    private var _moveMonth = 0

    val checkDateList = MutableLiveData<MutableList<LocalDate>>()

    private val _backEvent = MutableLiveData<Event<Unit>>()
    val backEvent: LiveData<Event<Unit>>
        get() = _backEvent

    private val _refreshEvent = MutableLiveData<Event<Unit>>()
    val refreshEvent: LiveData<Event<Unit>>
        get() = _refreshEvent

    init {
        checkDateList.value = mutableListOf()
        viewModelScope.async {
            loadMyInfo()
            refreshData()
        }
    }

    fun btnEvent(viewId: Int) {
        when (viewId) {
            R.id.my_attend_back -> { _backEvent.value = Event(Unit) }
            R.id.my_attend_refresh -> { _refreshEvent.value = Event(Unit); refreshData(); moveCurrentMonth(0) }
            R.id.my_attend_body_prev_month -> { moveCurrentMonth(-1) }
            R.id.my_attend_body_next_month -> { moveCurrentMonth(1) }
            R.id.my_attend_body_current_month -> { moveCurrentMonth(0) }
        }
    }

    private fun moveCurrentMonth(adjust: Int = _moveMonth) {
        val now = YearMonth.now()
        val current = currentMonth.value!!

        if (adjust == 0) {
            currentMonth.value = now
            _moveMonth = 0
        } else {
            if (adjust < 0) {
                if (_moveMonth < -11) return
                else currentMonth.value = current.minusMonths(1)
                _moveMonth--
            } else {
                if (_moveMonth >= 0) return
                else currentMonth.value = current.plusMonths(1)
                _moveMonth++
            }
        }
    }

    private fun loadMyInfo() {
        viewModelScope.async {
            val user = userRepository.getLoginData((userRepository.getLoginID()))
            name.value = user.name
            pos.value = user.position_name
        }
    }

    private fun refreshData() {
        currentMonth.value = YearMonth.now()
        val now = LocalDate.now()
        _dateStr.value = "${now.year}년 ${now.monthValue}월 ${now.dayOfMonth}일 기준"
        loadAttendList()
    }

    private fun loadAttendList() {
        viewModelScope.async {
            val attendList = userRepository.getAttendList(userRepository.getLoginID())
            val dateList = mutableListOf<LocalDate>()
            attendList.forEach {
                dateList.add(LocalDate.parse(it.date, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            }
            checkDateList.value = dateList
        }
    }

    private fun loadMonthAttendList(month: YearMonth) {
        viewModelScope.async {
            val attendList = userRepository.getAttendList(userRepository.getLoginID(), month.year, month.monthValue)
            _total.value = attendList.size.toString()
        }
    }

    val monthScrollListener = object: MonthScrollListener {
        override fun invoke(p1: CalendarMonth) {
            val current = currentMonth.value ?: YearMonth.now()
            if (p1.yearMonth.isBefore(current))
                _moveMonth--
            if (p1.yearMonth.isAfter(current))
                _moveMonth++
            currentMonth.value = p1.yearMonth
            loadMonthAttendList(p1.yearMonth)
        }
    }


    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AttendViewModel(UserRepository.getInstance(application)!!) as T
        }
    }
}