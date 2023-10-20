package com.sjk.yoram.presentation.main.my

import android.app.Application
import androidx.lifecycle.*
import com.sjk.yoram.R
import com.sjk.yoram.util.Event
import com.sjk.yoram.data.repository.ServerRepository
import com.sjk.yoram.data.repository.UserRepository
import kotlinx.coroutines.launch

class FragMyViewModel(private val userRepository: UserRepository, private val serverRepository: ServerRepository): ViewModel() {
    private val _maxWeek = MutableLiveData<Int>()
    val maxWeek: LiveData<Int>
        get() = _maxWeek

    private val _backEvent = MutableLiveData<Event<Unit>>()
    val backEvent: LiveData<Event<Unit>>
        get() = _backEvent

    private val _editEvent = MutableLiveData<Event<Unit>>()
    val editEvent: LiveData<Event<Unit>>
        get() = _editEvent

    private val _giveEvent = MutableLiveData<Event<Unit>>()
    val giveEvent: LiveData<Event<Unit>>
        get() = _giveEvent

    private val _attendEvent = MutableLiveData<Event<Unit>>()
    val attendEvent: LiveData<Event<Unit>>
        get() = _attendEvent

    private val _prefEvent = MutableLiveData<Event<Unit>>()
    val prefEvent: LiveData<Event<Unit>>
        get() = _prefEvent

    init {
        loadServerValues()
    }

    fun btnEvent(btnId: Int) {
        when(btnId) {
            R.id.frag_my_info_btn -> { _editEvent.value = Event(Unit) }
            R.id.frag_my_user_menus_give -> { _giveEvent.value = Event(Unit) }
            R.id.frag_my_user_menus_attend -> { _attendEvent.value = Event(Unit) }
            R.id.frag_my_header_pref, R.id.frag_my_pref -> { _prefEvent.value = Event(Unit) }
        }
    }


    private fun loadServerValues() = viewModelScope.launch {
        _maxWeek.value = serverRepository.getMaxWeekOfMonth()
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FragMyViewModel(UserRepository.getInstance(application)!!, ServerRepository.getInstance(application)!!) as T
        }
    }
}