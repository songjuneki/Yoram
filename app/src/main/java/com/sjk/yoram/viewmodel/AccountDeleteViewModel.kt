package com.sjk.yoram.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.google.android.material.textfield.TextInputLayout
import com.sjk.yoram.R
import com.sjk.yoram.model.Event
import com.sjk.yoram.model.dto.AccountDeleteInfo
import com.sjk.yoram.model.dto.UserDetail
import com.sjk.yoram.model.ui.listener.TextInputChanged
import com.sjk.yoram.repository.ServerRepository
import com.sjk.yoram.repository.UserRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.experimental.and
import kotlin.system.exitProcess

class AccountDeleteViewModel(private val serverRepository: ServerRepository, private val userRepository: UserRepository): ViewModel() {
    init {
        viewModelScope.launch {
            _myInfo = userRepository.getUserDetail(userRepository.getLoginID())
        }
    }

    private lateinit var _myInfo: UserDetail

    var currentFragment: AccountDeleteFragments = AccountDeleteFragments.NAME

    val inputName = MutableLiveData("")
    val inputBirth = MutableLiveData("")
    val inputPhone = MutableLiveData("")
    val inputPassword = MutableLiveData("")
    val inputPasswordValid = MutableLiveData("")
    val birthPickerYear = MutableLiveData(LocalDate.now().year)
    val birthPickerMonth = MutableLiveData(LocalDate.now().monthValue-1)
    val birthPickerDay = MutableLiveData(LocalDate.now().dayOfMonth)

    val passwordValidation = MutableLiveData(true)

    val variableBtnText = MutableLiveData("다음으로")

    val isNetworkProgress = MutableLiveData(false)

    private val _exitEvent = MutableLiveData<Event<Unit>>()
    val exitEvent: LiveData<Event<Unit>>
        get() = _exitEvent

    private val _backEvent = MutableLiveData<Event<Unit>>()
    val backEvent: LiveData<Event<Unit>>
        get() = _backEvent

    private val _changeFragmentEvent = MutableLiveData<Event<Int>>()
    val changeFragmentEvent: LiveData<Event<Int>>
        get() = _changeFragmentEvent

    private val _nextBtnCondition = MutableLiveData(false)
    val nextBtnCondition: LiveData<Boolean>
        get() = _nextBtnCondition

    private val _toastEvent = MutableLiveData<Event<String>>()
    val toastEvent: LiveData<Event<String>>
        get() = _toastEvent

    private val _shutdownEvent = MutableLiveData<Event<Unit>>()
    val shutdownEvent: LiveData<Event<Unit>>
        get() = _shutdownEvent


    val nameInputChanged = object: TextInputChanged {
        override fun afterTextChanged(view: TextInputLayout, input: String) {
            _nextBtnCondition.value = input == _myInfo.name
            if (input != _myInfo.name)
                view.error = "이름을 확인해 주세요"
            else
                view.error = ""
        }
    }

    val birthInputChanged = object: TextInputChanged {
        override fun afterTextChanged(view: TextInputLayout, input: String) {
            _nextBtnCondition.value = input == _myInfo.birth
            if (input != _myInfo.birth)
                view.error = "생년월일을 확인해 주세요"
            else
                view.error = ""
        }
    }

    val phoneInputChanged = object: TextInputChanged {
        override fun afterTextChanged(view: TextInputLayout, input: String) {
            _nextBtnCondition.value = input == _myInfo.phone
            if (input != _myInfo.phone)
                view.error = "번호를 확인해 주세요"
            else
                view.error = ""
        }
    }

    val passwordInputChanged = object: TextInputChanged {
        override fun afterTextChanged(view: TextInputLayout, input: String) {
            _nextBtnCondition.value = input.isNotEmpty()
            view.error = ""
        }
    }

    fun btnClick(btnId: Int) {
        when (btnId) {
            R.id.frag_my_pref_account_delete_back -> { if (currentFragment == AccountDeleteFragments.FINISH) exitProcess(0) else _exitEvent.value = Event(Unit) }
            R.id.frag_my_pref_account_delete_second_input_birth -> changeFragment(R.id.action_accountDeleteSecondFragment_to_accountDeleteBirthEditDialog)
            R.id.dialog_account_delete_bd_ok -> { inputBirth.value = "${birthPickerYear.value}-${String.format("%02d", birthPickerMonth.value?.plus(1))}-${String.format("%02d", birthPickerDay.value)}"; _backEvent.value = Event(Unit) }
            R.id.dialog_account_delete_bd_cancel -> _backEvent.value = Event(Unit)
        }
    }

    fun variableBtnClick() {
        when (currentFragment) {
            AccountDeleteFragments.NAME -> {
                changeFragment(R.id.action_accountDeleteFirst_to_accountDeleteSecondFragment)
                currentFragment = AccountDeleteFragments.BIRTHDAY
            }
            AccountDeleteFragments.BIRTHDAY -> {
                changeFragment(R.id.action_accountDeleteSecondFragment_to_accountDeleteThirdFragment)
                currentFragment = AccountDeleteFragments.PHONE
            }
            AccountDeleteFragments.PHONE -> {
                changeFragment(R.id.action_accountDeleteThirdFragment_to_accountDeleteFourthFragment)
                currentFragment = AccountDeleteFragments.PASSWORD
                passwordValidation.value = true
            }
            AccountDeleteFragments.PASSWORD -> {
                viewModelScope.launch {
                    val validation =
                        withContext(viewModelScope.coroutineContext) {
                            isNetworkProgress.value = true
                            userRepository.isValidAccount(
                                _myInfo.id,
                                this@AccountDeleteViewModel.encryptKey(inputPassword.value)
                            )
                        }
                    isNetworkProgress.value = false

                    passwordValidation.value = validation
                    if (validation) {
                        changeFragment(R.id.action_accountDeleteFourthFragment_to_accountDeleteFifthFragment)
                        currentFragment = AccountDeleteFragments.PASSWORD_VALID
                    }
                }
            }
            AccountDeleteFragments.PASSWORD_VALID -> {
                viewModelScope.launch {
                    val validation = withContext(viewModelScope.coroutineContext) {
                        isNetworkProgress.value = true
                        userRepository.isValidAccount(_myInfo.id, this@AccountDeleteViewModel.encryptKey(inputPasswordValid.value))
                    }
                    isNetworkProgress.value = false

                    passwordValidation.value = validation
                    if (validation) {
                        changeFragment(R.id.action_accountDeleteFifthFragment_to_accountDeleteSixthFragment)
                        currentFragment = AccountDeleteFragments.LAST_CHECK
                        variableBtnText.value = "동의 (계정 삭제)"
                        _nextBtnCondition.value = true
                    }
                }
            }
            AccountDeleteFragments.LAST_CHECK -> {
                viewModelScope.launch {
                    val info = AccountDeleteInfo(userRepository.getLoginID(), inputBirth.value!!, inputPhone.value!!, encryptKey(inputPasswordValid.value), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    val request = withContext(viewModelScope.coroutineContext) {
                        isNetworkProgress.value = true
                        userRepository.requestDeleteUser(info)
                    }
                    isNetworkProgress.value = false

                    if (request) {
                        userRepository.setIsInit(true)
                        userRepository.setLogin(-1, "@")
                        this@AccountDeleteViewModel.changeFragment(R.id.action_accountDeleteSixthFragment_to_accountDeleteFinishFragment)
                        currentFragment = AccountDeleteFragments.FINISH
                        variableBtnText.value = "앱 종료"
                    } else {
                        _toastEvent.value = Event("오류가 발생했습니다. 처음부터 다시 시도해 주세요")
                        _exitEvent.value = Event(Unit)
                    }
                }
            }
            AccountDeleteFragments.FINISH -> {
                _shutdownEvent.value = Event(Unit)
            }
        }
        _nextBtnCondition.value = currentFragment == AccountDeleteFragments.LAST_CHECK || currentFragment == AccountDeleteFragments.FINISH
    }

    private fun changeFragment(actionId: Int) {
        _changeFragmentEvent.value = Event(actionId)
    }

    private fun encryptKey(key: String?): String {
        if (key.isNullOrEmpty()) return ""

        val encoder = MessageDigest.getInstance("SHA-256")
        val byteArray = encoder.digest(key.toByteArray())

        val enc = StringBuffer()
        for (byte in byteArray) {
            val hashedByte = (byte.and((0xff).toByte()) + 0x100).toString(16)
            if (hashedByte.length > 2)
                enc.append(hashedByte.substring(1))
            else
                enc.append(hashedByte)
        }
        return enc.toString()
    }


    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AccountDeleteViewModel(ServerRepository.getInstance(application)!!, UserRepository.getInstance(application)!!) as T
        }
    }

    enum class AccountDeleteFragments { NAME, BIRTHDAY, PHONE, PASSWORD, PASSWORD_VALID, LAST_CHECK, FINISH }
}