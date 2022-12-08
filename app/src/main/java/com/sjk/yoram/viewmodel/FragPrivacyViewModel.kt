package com.sjk.yoram.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.sjk.yoram.model.Event
import com.sjk.yoram.model.dto.UserDetail
import com.sjk.yoram.model.dto.UserPrivacyPolicy
import com.sjk.yoram.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.math.ceil

class FragPrivacyViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _userDetail = MutableLiveData<UserDetail>()
    val userDetail: LiveData<UserDetail>
        get() = _userDetail

    lateinit var originalpp: UserPrivacyPolicy

    private val _pp = MutableLiveData<UserPrivacyPolicy>()
    val pp: LiveData<UserPrivacyPolicy>
        get() = _pp

    private val _applyEvent = MutableLiveData<Event<Unit>>()
    val applyEvent: LiveData<Event<Unit>>
        get() = _applyEvent

    private val _applySuccessEvent = MutableLiveData<Event<Unit>>()
    val applySuccessEvent: LiveData<Event<Unit>>
        get() = _applySuccessEvent

    private val _applyFailureEvent = MutableLiveData<Event<Unit>>()
    val applyFailureEvent: LiveData<Event<Unit>>
        get() = _applyFailureEvent

    init {
        viewModelScope.launch {
            originalpp = userRepository.getUserPrivacyPolicy()
            _userDetail.value = userRepository.getUserDetail(userRepository.getLoginID())
            _pp.value = userRepository.getUserPrivacyPolicy()
        }
    }

    fun editedPrivacyPolicy() {
        _pp.value = _pp.value!!
    }

    fun getMaskedName(): String {
        val name = _userDetail.value?.name ?: "..."
        var masked = name.substring(0, ceil(name.length/3.0).toInt())
        masked += "**"
        return masked
    }

    fun ppIsChanged(): Boolean {
        return !originalpp.isEqual(_pp.value)
    }

    fun loadPP() {
        viewModelScope.async {
            originalpp = userRepository.getUserPrivacyPolicy()
            _pp.value = userRepository.getUserPrivacyPolicy()
        }
    }

    fun applyAction() {
        _applyEvent.value = Event(Unit)
    }

    fun changedValueApply() {
        viewModelScope.async {
            val res = userRepository.editUserPrivacyPolicy(_pp.value ?: originalpp)
            if (res)
                _applySuccessEvent.value = Event(Unit)
            else
                _applyFailureEvent.value = Event(Unit)
            loadPP()
        }
    }


    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FragPrivacyViewModel(UserRepository.getInstance(application)!!) as T
        }
    }
}