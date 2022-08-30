package com.sjk.yoram.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.sjk.yoram.repository.ServerRepository
import com.sjk.yoram.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FragMyViewModel(private val userRepository: UserRepository, private val serverRepository: ServerRepository): ViewModel() {
    private val _maxWeek = MutableLiveData<Int>()
    val maxWeek: LiveData<Int>
        get() = _maxWeek

    init {
        loadServerValues()
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