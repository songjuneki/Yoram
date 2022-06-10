package com.sjk.yoram.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjk.yoram.model.MyLoginData
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

    suspend fun countdown() = viewModelScope.launch(start = CoroutineStart.LAZY) {
        _timer.value = 15
        while (true) {
            if (_timer.value == 0) {
                _timer.value = 15
                continue
            }
            _timer.value = _timer.value!!.minus(1)
            delay(1000L)
        }
    }


    suspend fun countstop() {
        if (this.countdown().isActive) this.countdown().cancel()
    }


}