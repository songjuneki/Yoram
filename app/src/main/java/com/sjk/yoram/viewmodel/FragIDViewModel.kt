package com.sjk.yoram.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjk.yoram.model.dto.MyLoginData
import kotlinx.coroutines.*

class FragIDViewModel: ViewModel() {
    private val _timer = MutableLiveData<Int>()
    val timer: LiveData<Int> get() = _timer


    private val _user = MutableLiveData<MyLoginData>()
    val user: LiveData<MyLoginData> get() = _user


    init {
        _timer.value = 15
        _user.value = MyLoginData()
    }

    fun setUser(user: MyLoginData) {
        _user.postValue(user)
    }

    val countJob = viewModelScope.launch(start = CoroutineStart.LAZY) {
        while (isActive) {
            if (_timer.value!! == 0)  {
                _timer.value = 15
                delay(1000L)
            }
            _timer.value = _timer.value!!.minus(1)
            delay(1000L)
        }
        _timer.value = 15
    }

    suspend fun countdown() = viewModelScope.launch(start = CoroutineStart.LAZY) {
        this@FragIDViewModel.countJob.join()
        _timer.value = 15
    }


    fun countstop() {
        _timer.value = 15
    }


}