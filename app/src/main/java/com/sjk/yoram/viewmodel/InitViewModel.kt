package com.sjk.yoram.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputLayout
import com.sjk.yoram.R
import com.sjk.yoram.model.*
import com.sjk.yoram.model.dto.Juso
import com.sjk.yoram.model.ui.listener.AddressItemClickListener
import com.sjk.yoram.model.ui.listener.TextInputChanged
import com.sjk.yoram.repository.ServerRepository
import com.sjk.yoram.repository.UserRepository
import kotlinx.coroutines.*
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.experimental.and

class InitViewModel(private val userRepository: UserRepository, private val serverRepository: ServerRepository): ViewModel() {
    private val _backBtnEvent = MutableLiveData<Event<Unit>>()
    val backBtnEvent: LiveData<Event<Unit>>
        get() = _backBtnEvent

    private val _progressEvent = MutableLiveData<Event<Boolean>>()
    val progressEvent: LiveData<Event<Boolean>>
        get() = _progressEvent

    private val _currentFragment = MutableLiveData<InitFragmentType>()
    val currentFragment: LiveData<InitFragmentType>
        get() = _currentFragment

    private val _naviActon = MutableLiveData<Event<Int>>()
    val naviAction: LiveData<Event<Int>>
        get() = _naviActon

    private val _loginEvent = MutableLiveData<Event<LoginState>>()
    val loginEvent: LiveData<Event<LoginState>>
        get() = _loginEvent

    private val _anonymousLoginEvent = MutableLiveData<Event<Unit>>()
    val anonymousLoginEvent: LiveData<Event<Unit>>
        get() = _anonymousLoginEvent

    private val _msgEvent = MutableLiveData<Event<String>>()
    val msgEvent: LiveData<Event<String>>
        get() = _msgEvent

    val loginBirth = MutableLiveData("")


    private val _newUser = NewUser()

    val newName = MutableLiveData<String>()
    val newPw = MutableLiveData<String>()
    val newPwV = MutableLiveData<String>()
    val newSex = MutableLiveData<SexState>()
    val newBD = MutableLiveData<String>()

    val newPhone = MutableLiveData<String>()
    val newTel = MutableLiveData<String>()
    val newAdd = MutableLiveData<String>()
    val newAddMore = MutableLiveData<String>()
    val newCarNo = MutableLiveData<String>()

    val isReqDoneList = MutableLiveData<MutableList<Boolean>>()
    val isMoreReqDoneList = MutableLiveData<MutableList<Boolean>>()


    private val _addrKeyword = MutableLiveData<String>()
    val addrKeyword: LiveData<String>
        get() = _addrKeyword

    private val _addrSearchResult = MutableLiveData<List<Juso>>()
    val addrSearchResult: LiveData<List<Juso>>
        get() = _addrSearchResult


    init {
        initialize()
    }

    fun initialize() {
        _currentFragment.value = InitFragmentType.InitFragment_HOME
        _loginEvent.value = Event(LoginState.NONE)
        newName.value = ""
        newPw.value = ""
        newPwV.value = ""
        newSex.value = SexState.NONE
        newBD.value = ""

        newPhone.value = ""
        newTel.value = ""
        newAdd.value = ""
        newAddMore.value = ""
        newCarNo.value = ""
        _addrKeyword.value = ""


        isReqDoneList.value = mutableListOf(false, false, false, false, false)
        isMoreReqDoneList.value = mutableListOf(false, true, false, false, true, false, false)
    }

    private fun changeFragment(actionId: Int, fragmentType: InitFragmentType) {
        _currentFragment.value = fragmentType
        _naviActon.value = Event(actionId)
    }


    fun btnClick(btnId: Int) {
        when(btnId) {
            R.id.init_back_btn -> _backBtnEvent.value = Event(Unit)
            R.id.dialog_bd_cancel -> _backBtnEvent.value = Event(Unit)
            R.id.dialog_address_close -> {_backBtnEvent.value = Event(Unit); _addrKeyword.value = ""; _addrSearchResult.value = listOf()}

            R.id.init_go_login_btn -> changeFragment(R.id.action_initHome_to_initLogin, InitFragmentType.InitFragment_LOGIN)

            R.id.init_go_signup_btn -> changeFragment(R.id.action_initHome_to_initSignUp, InitFragmentType.InitFragment_SIGNUP)

            R.id.init_go_anonymous_btn -> {
                _anonymousLoginEvent.value = Event(Unit)
                userRepository.setIsInit(false)
            }

            R.id.init_login_bd_et -> { changeFragment(R.id.action_initLoginFragment_to_initBDDialogFragment, InitFragmentType.InitFragment_LOGIN) }
            R.id.init_login_signup_tv -> changeFragment(R.id.action_initLogin_to_initSignup, InitFragmentType.InitFragment_SIGNUP)

            R.id.init_signup_bd_et -> changeFragment(R.id.action_initSignup_to_dialogBD, InitFragmentType.InitFragment_Dialog_BD)
            R.id.init_signup_address_et -> changeFragment(R.id.action_initSignUpAdd_to_dialogAdd, InitFragmentType.InitFragment_Dialog_ADD)

            R.id.init_signup_rule_link -> changeFragment(R.id.action_initSignUpAdd_to_dialogAgree, InitFragmentType.InitFragment_Dialog_APP_RULE)
            R.id.init_signup_privacy_rule_link -> changeFragment(R.id.action_initSignUpAdd_to_dialogAgree, InitFragmentType.InitFragment_Dialog_PRIVACY_RULE)

            R.id.init_signup_complete_btn -> btnSignUpComplete()

            R.id.init_login_need_help -> showToastMsg("현재 기능 준비중입니다. 관리자에게 문의해주세요")
            R.id.init_login_find_pw -> changeFragment(R.id.action_initLoginFragment_to_initFindPWFragment, InitFragmentType.InitFragment_FIND_PW)

            R.id.frag_init_find_pw_back_btn, R.id.frag_init_find_pw_alert_back_btn -> _backBtnEvent.value = Event(Unit)
        }
    }


    fun sexBtnClick(group: MaterialButtonToggleGroup, checkedId: Int, isChecked: Boolean) {
        when (checkedId) {
            R.id.init_signup_sex_male_btn -> {
                if (isChecked) newSex.value = SexState.MALE
                else newSex.value = SexState.NONE
                setRequireBoolean(isReqDoneList, newSex.value != SexState.NONE, 3)
            }
            R.id.init_signup_sex_female_btn -> {
                if (isChecked) newSex.value = SexState.FEMALE
                else newSex.value = SexState.NONE
                setRequireBoolean(isReqDoneList, newSex.value != SexState.NONE, 3)
            }
        }
    }


    fun setBD(birth: String) {
        newBD.value = birth
        setRequireBoolean(isReqDoneList, true, 4)
    }

    private fun setRequireBoolean(list: MutableLiveData<MutableList<Boolean>>, bool: Boolean, index: Int) {
        val temp = list.value!!
        temp[index] = bool
        list.postValue(temp)
    }


    val nameInputChanged = object: TextInputChanged {
        override fun afterTextChanged(view: TextInputLayout, input: String) {
            val regex = Regex("^[가-힣]+$")
            if (input.isEmpty()) {
                view.error = ""
                setRequireBoolean(isReqDoneList, false, 0)
            } else if (input.matches(regex)) {
                view.error = ""
                setRequireBoolean(isReqDoneList, true, 0)
            } else {
                view.error = "올바른 이름을 입력해 주세요"
                setRequireBoolean(isReqDoneList, false, 0)
            }
        }
    }

    val pwInputChanged = object: TextInputChanged {
        override fun afterTextChanged(view: TextInputLayout, input: String) {
            val regex = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
            if (input.isEmpty()) {
                view.error = ""
                setRequireBoolean(isReqDoneList, false, 1)
            } else if(input.matches(regex)) {
                view.error = ""
                setRequireBoolean(isReqDoneList, true, 1)
            } else {
                view.error = "영문+숫자 8자리를 조합하여 입력해주세요"
                setRequireBoolean(isReqDoneList, false, 1)
            }
        }
    }

    val pwValidInputChanged = object: TextInputChanged {
        override fun afterTextChanged(view: TextInputLayout, input: String) {
            val pw = newPw.value
            if (input.isEmpty()) {
                view.error = ""
                setRequireBoolean(isReqDoneList, false, 2)
            } else if (input == pw) {
                view.error = ""
                setRequireBoolean(isReqDoneList, true, 2)
            } else {
                view.error = "비밀번호가 일치하지 않습니다."
                setRequireBoolean(isReqDoneList, false, 2)
            }
        }
    }

    val phoneInputChanged = object: TextInputChanged {
        override fun afterTextChanged(view: TextInputLayout, input: String) {
            val regex = Regex("^\\d{3}-\\d{4}-\\d{4}$")
            if (input.isEmpty()) {
                view.error = ""
                setRequireBoolean(isMoreReqDoneList, false, 0)
            } else if (input.matches(regex)) {
                view.error = ""
                setRequireBoolean(isMoreReqDoneList, true, 0)
            } else {
                view.error = "올바른 휴대폰 번호를 입력해 주세요"
                setRequireBoolean(isMoreReqDoneList, false, 0)
            }
        }
    }

    val telInputChanged = object: TextInputChanged {
        override fun afterTextChanged(view: TextInputLayout, input: String) {
            val regex = Regex("^\\d{2,3}-\\d{3,4}-\\d{3,4}$")
            if (input.isEmpty()) {
                view.error = ""
                setRequireBoolean(isMoreReqDoneList, true, 1)
            } else if (input.matches(regex)) {
                view.error = ""
                setRequireBoolean(isMoreReqDoneList, true, 1)
            } else {
                view.error = "올바른 휴대폰 번호를 입력해 주세요"
                setRequireBoolean(isMoreReqDoneList, false, 1)
            }
        }
    }

    val addrSearchInputChanged = object: TextInputChanged {
        override fun afterTextChanged(view: TextInputLayout, input: String) {
            _addrKeyword.value = input
            if (input.isEmpty()) _addrSearchResult.value = listOf()
            addrSearchJob?.cancel()
            searchAddr(input)
        }
    }

    private var addrSearchJob: Job? = null

    val addrItemClickListener = object: AddressItemClickListener {
        override fun onClick(juso: Juso) {
            newAdd.value = juso.roadAddr
            _addrKeyword.value = ""
            _addrSearchResult.value = listOf()
            _backBtnEvent.value = Event(Unit)
            setRequireBoolean(isMoreReqDoneList, true, 2)
        }
    }

    private fun searchAddr(keyword: String) {
        addrSearchJob = viewModelScope.launch {
            val result = serverRepository.searchAddress(keyword)
            _addrSearchResult.value = result
        }
    }

    val addrMoreInputChanged = object: TextInputChanged {
        override fun afterTextChanged(view: TextInputLayout, input: String) {
            if (input.isEmpty()) {
                view.error = ""
                setRequireBoolean(isMoreReqDoneList, false, 3)
            } else {
                view.error = ""
                setRequireBoolean(isMoreReqDoneList, true, 3)
            }
        }
    }


    val carNoInputChanged = object: TextInputChanged {
        override fun afterTextChanged(view: TextInputLayout, input: String) {
            val regex = Regex("^\\d{1,3}\\s*[가-힣]{1,2}\\s*\\d{0,4}$")
            if (input.isEmpty()) {
                view.error = ""
                setRequireBoolean(isMoreReqDoneList, true, 4)
            }else if (input.matches(regex)) {
                view.error = ""
                setRequireBoolean(isMoreReqDoneList, true, 4)
            } else {
                view.error = "올바른 차 번호를 입력해주세요"
                setRequireBoolean(isMoreReqDoneList, false, 4)
            }
        }
    }

    fun checkAgree(list: MutableLiveData<MutableList<Boolean>>, bool: Boolean, index: Int) {
        if (index == 5) _newUser.app_agree_date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        if (index == 6) _newUser.privacy_agree_date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        setRequireBoolean(list, bool, index)
    }

    fun agreeBtn(currentFragmentType: InitFragmentType) {
        when (currentFragmentType) {
            InitFragmentType.InitFragment_Dialog_APP_RULE -> checkAgree(isMoreReqDoneList, true, 5)
            InitFragmentType.InitFragment_Dialog_PRIVACY_RULE -> checkAgree(isMoreReqDoneList, true, 6)
            else -> {}
        }
        _backBtnEvent.value = Event(Unit)
    }

    val inputErrorChange = object: TextInputChanged {
        override fun afterTextChanged(view: TextInputLayout, input: String) {
            view.error = ""
        }
    }


    fun btnLogin(name: String, pw: String) {
        btnLogin(name, EncryptKey(pw), "")
    }
    fun btnLogin(name: String, pw: String, bd: String = "") {
        viewModelScope.async {
            showLoading()
            val loginResult = userRepository.loginUser(name, pw, bd)
            _loginEvent.value = Event(loginResult)
            hideLoading()
        }
    }

    fun btnSignUp() {
        _newUser.name = newName.value!!
        _newUser.pw = EncryptKey(newPw.value!!)
        _newUser.sex = newSex.value == SexState.MALE
        _newUser.birth = newBD.value!!
        changeFragment(R.id.action_initSignUp_to_initSignUpAdd, InitFragmentType.InitFragment_SIGNUP_ADD)
    }



    private fun btnSignUpComplete() {
        viewModelScope.launch {
            showLoading()
            val isCreated = userRepository.findUserId(newName.value ?: "", newBD.value ?: "") >= 0
            if (isCreated) {
                hideLoading()
                showToastMsg("이미 존재하는 계정 입니다.")
                return@launch
            }

            val newUserId = userRepository.userSignUp(
                _newUser.copy(
                    phone = newPhone.value ?: "",
                    tel = newTel.value ?: "",
                    address = newAdd.value ?: "",
                    address_more = newAddMore.value ?: "",
                    car = newCarNo.value ?: ""
                )
            )

            if (newUserId < 0) {
                hideLoading()
                showToastMsg("계정 생성에 오류가 발생했습니다. 다시 시도해주세요")
                return@launch
            }

            hideLoading()
            btnLogin(_newUser.name, EncryptKey(newPw.value ?: _newUser.pw), newBD.value ?: "")
        }
    }


    private fun hideLoading() {
        this._progressEvent.value = Event(false)
    }

    private fun showLoading() {
        this._progressEvent.value = Event(true)
    }

    private fun showToastMsg(msg: String) {
        this._msgEvent.value = Event(msg)
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return InitViewModel(UserRepository.getInstance(application)!!, ServerRepository.getInstance(application)!!) as T
        }
    }

    private fun EncryptKey(key: String): String {
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
}