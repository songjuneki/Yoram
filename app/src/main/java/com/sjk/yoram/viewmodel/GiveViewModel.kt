package com.sjk.yoram.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.sjk.yoram.R
import com.sjk.yoram.model.Event
import com.sjk.yoram.model.GiveListItem
import com.sjk.yoram.model.ui.adapter.GiveListAdapter
import com.sjk.yoram.repository.ServerRepository
import com.sjk.yoram.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.LocalDate

class GiveViewModel(private val userRepository: UserRepository, private val serverRepository: ServerRepository): ViewModel() {
    val name = MutableLiveData<String>()
    val pos = MutableLiveData<String>()

    private val _total = MutableLiveData<String>()
    val total: LiveData<String>
        get() = _total

    private val _currentMonth = MutableLiveData<Int>()
    val currentMonth: LiveData<Int>
        get() = _currentMonth

    private val _dateStr = MutableLiveData<String>()
    val dateStr: LiveData<String>
        get() = _dateStr

    private val _giveItemList = MutableLiveData<MutableList<GiveListItem>>(mutableListOf())
    val giveItemList: LiveData<MutableList<GiveListItem>>
        get() = _giveItemList

    private val _backEvent = MutableLiveData<Event<Unit>>()
    val bakcEvent: LiveData<Event<Unit>>
        get() = _backEvent

    private val _refreshEvent = MutableLiveData<Event<Unit>>()
    val refreshEvent: LiveData<Event<Unit>>
        get() = _refreshEvent

    init {
        viewModelScope.launch {
            loadMyInfo()
            loadCurrentDate()
            loadMyGives(LocalDate.now())
        }
    }

    fun btnEvent(viewId: Int) {
        when (viewId) {
            R.id.my_give_back -> { _backEvent.value = Event(Unit) }
            R.id.my_give_refresh -> { _refreshEvent.value = Event(Unit); loadCurrentDate(); loadMyGives(LocalDate.now()) }
        }
    }

    fun giveAdapter(): GiveListAdapter = GiveListAdapter()

    private fun loadMyInfo() {
        viewModelScope.async {
            val user = userRepository.getLoginData((userRepository.getLoginID()))
            name.value = user.name
            pos.value = user.position_name
        }
    }

    private fun loadCurrentDate() {
        viewModelScope.async {
            val now = LocalDate.now()
            _dateStr.value = "${now.year}년 ${now.monthValue}월 ${now.dayOfMonth}일 기준"
            _currentMonth.value = now.monthValue
        }
    }

    private fun loadMyGives(date: LocalDate) {
        viewModelScope.async {
            val format = DecimalFormat("###,###")
            val amount = userRepository.getCurrentMonthGiveAmount(userRepository.getLoginID())
            _total.value = format.format(amount)
            _giveItemList.value = userRepository.getUserGiveList(userRepository.getLoginID(), date)
        }
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GiveViewModel(UserRepository.getInstance(application)!!, ServerRepository.getInstance(application)!!) as T
        }
    }
}