package com.sjk.yoram.viewmodel

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.sjk.yoram.model.Event
import com.sjk.yoram.repository.ServerRepository
import com.sjk.yoram.repository.UserRepository

class InitViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _btnEvent = MutableLiveData<Event<Int>>()
    val btnEvent: LiveData<Event<Int>>
        get() = _btnEvent

    val inputName = MutableLiveData<String>()
    val inputPw = MutableLiveData<String>()

    fun btnClicked(resId: Int) {
        _btnEvent.value = Event(resId)
    }

    fun btnLogin(id: String, pw: String) {
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return InitViewModel(UserRepository.getInstance(application)!!) as T
        }
    }
}