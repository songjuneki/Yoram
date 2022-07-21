package com.sjk.yoram.viewmodel

import android.app.Application
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.databinding.adapters.TextViewBindingAdapter
import androidx.lifecycle.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sjk.yoram.R
import com.sjk.yoram.model.*
import com.sjk.yoram.repository.ServerRepository
import com.sjk.yoram.repository.UserRepository

class InitViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _backBtnEvent = MutableLiveData<Event<Unit>>()
    val backBtnEvent: LiveData<Event<Unit>>
        get() = _backBtnEvent

    private val _anonymousBtnEvent = MutableLiveData<Event<Unit>>()
    val anonymousBtnEvent: LiveData<Event<Unit>>
        get() = _anonymousBtnEvent

    private val _currentFragment = MutableLiveData<InitFragmentType>()
    val currentFragment: LiveData<InitFragmentType>
        get() = _currentFragment

    private val _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title

    private val _naviActon = MutableLiveData<Event<Int>>()
    val naviAction: LiveData<Event<Int>>
        get() = _naviActon

    private val _newUser = NewUser()

    val newName = MutableLiveData<String>()
    val newPw = MutableLiveData<String>()
    val newPwV = MutableLiveData<String>()
    val newSex = MutableLiveData<SexState>()
    val newBD = MutableLiveData<String>()

    init {
        initialize()
    }

    fun initialize() {
        _currentFragment.value = InitFragmentType.InitFragment_HOME
        newName.value = ""
        newPw.value = ""
        newPwV.value = ""
        newSex.value = SexState.NONE
        newBD.value = ""
    }

    private fun changeFragment(actionId: Int, fragmentType: InitFragmentType) {
        _currentFragment.value = fragmentType
        _naviActon.value = Event(actionId)
    }

    fun btnClick(btnId: Int) {
        when(btnId) {
            R.id.init_back_btn -> _backBtnEvent.value = Event(Unit)

            R.id.init_go_login_btn -> changeFragment(R.id.action_initHome_to_initLogin, InitFragmentType.InitFragment_LOGIN)
            R.id.init_go_signup_btn -> changeFragment(R.id.action_initHome_to_initSignUp, InitFragmentType.InitFragment_SIGNUP)

            R.id.init_go_anonymous_btn -> _anonymousBtnEvent.value = Event(Unit)

            R.id.init_login_signup_tv -> changeFragment(R.id.action_initLogin_to_initSignup, InitFragmentType.InitFragment_SIGNUP)

            R.id.init_signup_bd_et -> changeFragment(R.id.action_initSignup_to_dialogBD, InitFragmentType.InitFragment_Dialog_BD)
        }
    }

    fun newInputCheck() {

    }

    /* Not used
    fun sexBtnClick(group: MaterialButtonToggleGroup, checkedId: Int, isChecked: Boolean) {
        when (checkedId) {
            R.id.init_signup_sex_male_btn -> {
                if (newSex.value != SexState.MALE) {
                    if (isChecked) newSex.value = SexState.MALE
                }
                else newSex.value = SexState.NONE
            }
            R.id.init_signup_sex_female_btn -> {
                if (newSex.value != SexState.FEMALE) {
                    if (isChecked) newSex.value = SexState.FEMALE
                }
                else newSex.value = SexState.NONE
            }
        }
    }
    */

    fun setBD(birth: String) {
        newBD.value = birth
    }


    fun isNewUserDone(): Boolean {
        if (newName.value!!.isNotEmpty() && newPw.value!!.isNotEmpty() && newPwV.value!!.isNotEmpty() && newSex.value!! != SexState.NONE && newBD.value!!.isNotEmpty())
            return true
        return false
    }

    val nameInputChanged = object: TextInputChanged {
        override fun onTextChanged(view: TextInputLayout, input: String) {
            val regex = Regex("^[가-힣]+$")
            if (input.isEmpty())
                view.error = ""
            else if (input.matches(regex))
                view.error = ""
            else
                view.error = "올바른 이름을 입력해 주세요"
        }
    }

    val pwInputChanged = object: TextInputChanged {
        override fun onTextChanged(view: TextInputLayout, input: String) {
            val regex = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
            if (input.isEmpty() || input.matches(regex))
                view.error = ""
            else
                view.error = "영문+숫자 8자리를 조합하여 입력해주세요"
        }
    }

    val pwValidInputChanged = object: TextInputChanged {
        override fun onTextChanged(view: TextInputLayout, input: String) {
            val pw = newPw.value
            if (input.isEmpty() || input == pw)
                view.error = ""
            else
                view.error = "비밀번호가 일치하지 않습니다."
        }
    }

    fun btnLogin(id: String, pw: String) {
        Log.d("JKJK", "Login id:$id, pw:$pw")
    }

    fun btnSignup() {
        _newUser.name = newName.value ?: ""
        _newUser.pw = newPw.value ?: ""
        _newUser.pw = AESUtil().Encrypt(_newUser.pw)
        _newUser.sex = newSex.value ?: SexState.MALE
        _newUser.birth = newBD.value ?: ""
        Log.d("JKJK", "Sign up ${newName.value}, ${newPw.value}, ${newSex.value}, ${newBD.value}")
        Log.d("JKJK", "Sign up newUser=$_newUser")
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return InitViewModel(UserRepository.getInstance(application)!!) as T
        }
    }
}