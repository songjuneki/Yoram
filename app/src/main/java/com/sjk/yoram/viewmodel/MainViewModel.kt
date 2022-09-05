package com.sjk.yoram.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import com.sjk.yoram.model.*
import com.sjk.yoram.model.dto.MyLoginData
import com.sjk.yoram.repository.ServerRepository
import com.sjk.yoram.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class MainViewModel(private val userRepository: UserRepository, private val serverRepository: ServerRepository): ViewModel() {
    private val _currentFragmentType = MutableLiveData(FragmentType.Fragment_HOME)
    val currentFragmentType: LiveData<FragmentType> = _currentFragmentType
    val dptClickState = MutableLiveData<Boolean>(false)
    val loginState = MutableLiveData(LoginState.NONE)

    private val _loginData = MutableLiveData<MyLoginData>()
    val loginData: LiveData<MyLoginData>
        get() = _loginData

    private val _avatar = MutableLiveData<Bitmap>()
    val avatar: LiveData<Bitmap>
        get() = _avatar

    private val _giveAmount = MutableLiveData<String>()
    val giveAmount: LiveData<String>
        get() = _giveAmount

    private val _currentFragment = MutableLiveData<Event<FragmentType>>()
    val currentFragment: LiveData<Event<FragmentType>>
        get() = _currentFragment


    init {
        loadLoginData()
        loadGiveAmount()
    }

    fun loadLoginData() {
        viewModelScope.launch {
            val id = userRepository.getLoginID()
            if (id == -1) {
                _loginData.value = MyLoginData()
                loginState.value = LoginState.NONE
            } else {
                _loginData.value = userRepository.getLoginData(id)
                _avatar.value = userRepository.getAvatarBitmap()
                loginState.value = LoginState.LOGIN
            }
        }
    }

    private fun loadGiveAmount() {
        viewModelScope.async {
            val id = userRepository.getLoginID()
            var total = BigInteger("0")
            if (id != -1)
                total = userRepository.getCurrentMonthGiveAmount(id)
            val moneyFormat = DecimalFormat("#,###")
            _giveAmount.value = moneyFormat.format(total)
        }
    }

    fun login() {
    }


    fun getUserPermission(): UserPermission {
        val data = _loginData.value ?: MyLoginData()
        Log.d("JKJK", "myPermission=${data.permission}")
        return UserPermission.values()[data.permission]
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(UserRepository.getInstance(application)!!, ServerRepository.getInstance(application)!!) as T
        }
    }
}