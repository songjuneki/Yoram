package com.sjk.yoram.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.sjk.yoram.R
import com.sjk.yoram.model.Event
import com.sjk.yoram.model.UserPermission
import com.sjk.yoram.repository.UserRepository
import kotlinx.coroutines.launch

class PrefViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _userPermission = MutableLiveData<UserPermission>()
    val userPermission: LiveData<UserPermission>
        get() = _userPermission

    private val _toastEvent = MutableLiveData<Event<String>>()
    val toastEvent: LiveData<Event<String>>
        get() = _toastEvent

    private val _naviEvent = MutableLiveData<Event<Int>>()
    val naviEvent: LiveData<Event<Int>>
        get() = _naviEvent

    private val _backEvent = MutableLiveData<Event<Unit>>()
    val backEvent: LiveData<Event<Unit>>
        get() = _backEvent

    private val _applyEvent = MutableLiveData<Event<Unit>>()
    val applyEvent: LiveData<Event<Unit>>
        get() = _applyEvent

    private val _applyCancelEvent = MutableLiveData<Event<Unit>>()
    val applyCancelEvent: LiveData<Event<Unit>>
        get() = _applyCancelEvent

    private val _ruleType = MutableLiveData<RuleType>()
    val ruleType: LiveData<RuleType>
        get() = _ruleType

    private val _logoutEvent = MutableLiveData<Event<Unit>>()
    val logoutEvent: LiveData<Event<Unit>>
        get() = _logoutEvent

    init {
        viewModelScope.launch {
            _userPermission.value = UserPermission.values()[userRepository.getMyPermission(userRepository.getLoginID())]
        }
    }

    fun btnEvent(btnId: Int) {
        when (btnId) {
            R.id.frag_my_pref_noti_push -> { showToast("기능 준비중입니다.") }
            R.id.frag_my_pref_rule_rule -> { moveFragment(R.id.action_prefFragment_to_prefRuleFragment); _ruleType.value = RuleType.APP }
            R.id.frag_my_pref_rule_privacy -> { moveFragment(R.id.action_prefFragment_to_prefRuleFragment); _ruleType.value = RuleType.PRIVACY }
            R.id.frag_my_pref_account_privacy -> { moveFragment(R.id.action_prefFragment_to_prefPrivacyFragment) }
            R.id.frag_my_pref_account_logout -> { moveFragment(R.id.action_prefFragment_to_prefLogoutDialogFragment) }
            R.id.frag_my_pref_admin_banner -> { moveFragment(R.id.action_prefFragment_to_adminBannerFragment) }
            R.id.dialog_my_pref_logout_ok -> { _logoutEvent.value = Event(Unit) }

            R.id.dialog_my_pref_apply_cancel -> { _applyCancelEvent.value = Event(Unit) }
        }
    }

    fun backBtn() {
        _backEvent.value = Event(Unit)
    }

    fun changedValueApply() {
        _applyEvent.value = Event(Unit)
    }


    private fun showToast(message: String) {
        _toastEvent.value = Event(message)
    }

    private fun moveFragment(actionId: Int) {
        _naviEvent.value = Event(actionId)
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PrefViewModel(UserRepository.getInstance(application)!!) as T
        }
    }

    enum class RuleType { APP, PRIVACY }
}