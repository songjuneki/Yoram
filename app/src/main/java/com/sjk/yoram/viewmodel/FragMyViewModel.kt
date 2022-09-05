package com.sjk.yoram.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.sjk.yoram.R
import com.sjk.yoram.model.Event
import com.sjk.yoram.repository.ServerRepository
import com.sjk.yoram.repository.UserRepository
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

    init {
        loadServerValues()
    }

    fun btnEvent(btnId: Int) {
        when(btnId) {
            R.id.frag_my_info_btn -> { _editEvent.value = Event(Unit) }
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