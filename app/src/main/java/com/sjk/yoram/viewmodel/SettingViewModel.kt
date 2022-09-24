package com.sjk.yoram.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.sjk.yoram.R
import com.sjk.yoram.model.Event
import com.sjk.yoram.model.UserPermission
import com.sjk.yoram.repository.UserRepository
import kotlinx.coroutines.launch

class SettingViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _userPermission = MutableLiveData<UserPermission>()
    val userPermission: LiveData<UserPermission>
        get() = _userPermission

    private val _toastEvent = MutableLiveData<Event<String>>()
    val toastEvent: LiveData<Event<String>>
        get() = _toastEvent

    private val _naviEvent = MutableLiveData<Event<Int>>()
    val naviEvent: LiveData<Event<Int>>
        get() = _naviEvent

    init {
        viewModelScope.launch {
            _userPermission.value = UserPermission.values()[userRepository.getMyPermission(userRepository.getLoginID())]
        }
    }

    fun btnEvent(btnId: Int) {
        when (btnId) {
            R.id.frag_my_setting_noti_push -> { showToast("기능 준비중입니다.") }
            R.id.frag_my_setting_rule_rule -> { moveFragment(0) }
        }
    }

    private fun showToast(message: String) {
        _toastEvent.value = Event(message)
    }

    private fun moveFragment(actionId: Int) {
        _naviEvent.value = Event(actionId)
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingViewModel(UserRepository.getInstance(application)!!) as T
        }
    }
}