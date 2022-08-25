package com.sjk.yoram.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import com.github.sumimakito.awesomeqr.AwesomeQrRenderer
import com.github.sumimakito.awesomeqr.option.RenderOption
import com.github.sumimakito.awesomeqr.option.color.Color
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.sjk.yoram.R
import com.sjk.yoram.model.AESUtil
import com.sjk.yoram.model.dto.MyLoginData
import com.sjk.yoram.repository.ServerRepository
import com.sjk.yoram.repository.UserRepository
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class FragIDViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _user = MutableLiveData<MyLoginData>()
    val user: LiveData<MyLoginData>
        get() = _user

    private val _isValidCode = MutableLiveData<Boolean>()
    val isValidCode: LiveData<Boolean>
        get() = _isValidCode

    private val _timer = MutableLiveData<Int>()
    val timer: LiveData<Int>
        get() = _timer

    private val _code = MutableLiveData<Bitmap>()
    val code: LiveData<Bitmap>
        get() = _code


    init {
        viewModelScope.launch {
            _timer.value = 0
            _user.value = userRepository.getLoginData(userRepository.getLoginID())
            countStop()
        }
    }

    fun btnEvent(id: Int) {
        when (id) {
            R.id.frag_id_refresh -> { makeCode(); countDown() }
        }
    }


    private fun makeCode() {
        viewModelScope.launch {
            _code.value = userRepository.getUserCode()
            _isValidCode.value = true
        }
    }

    private fun makeNotValidCode() {
        _code.value = userRepository.getNotValidUserCode()
    }


    var countDownJob: Job? = null
    fun countDown() {
        countDownJob = viewModelScope.launch {
            _timer.value = 16
            while (isActive) {
                if (_timer.value!! == 0)  {
                    countStop()
                    return@launch
                }
                _timer.value = _timer.value!!.minus(1)
                delay(1000L)
            }
        }
    }

    fun countStop() {
        countDownJob?.cancel()
        _isValidCode.value = false
        makeNotValidCode()
    }


    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FragIDViewModel(UserRepository.getInstance(application)!!) as T
        }
    }
}