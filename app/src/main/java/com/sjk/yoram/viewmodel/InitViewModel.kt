package com.sjk.yoram.viewmodel

import android.app.Application
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.sjk.yoram.R
import com.sjk.yoram.model.Event
import com.sjk.yoram.model.InitFragmentType
import com.sjk.yoram.model.SexState
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

    val isloginPwHide = MutableLiveData<Boolean>()

    val inputName = MutableLiveData<String>()
    val inputPw = MutableLiveData<String>()
    val inputPwv = MutableLiveData<String>()
    val sex = MutableLiveData<SexState>()
    val maleCheck = MutableLiveData<Boolean>()
    val femaleCheck = MutableLiveData<Boolean>()
    val inputBD = MutableLiveData<String>()

    init {
        _currentFragment.value = InitFragmentType.InitFragment_HOME
        isloginPwHide.value = true
        sex.value = SexState.NONE
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
        }
    }

    fun secureChanged(view: View) {
        when(view.id) {
            R.id.init_login_pw_visible_btn -> {
                isloginPwHide.value = !isloginPwHide.value!!
                if (isloginPwHide.value!!)
                    (view as MaterialButton).setIconResource(R.drawable.ic_password_show)
                else
                    (view as MaterialButton).setIconResource(R.drawable.ic_password_hide)
            }
        }
    }

    fun sexBtnClick(group: MaterialButtonToggleGroup, checkedId: Int, isChecked: Boolean) {
        when (checkedId) {
            R.id.init_signup_sex_male_btn -> {
                if (sex.value != SexState.MALE) {
                    if (isChecked) sex.value = SexState.MALE
                }
                else sex.value = SexState.NONE
            }
            R.id.init_signup_sex_female_btn -> {
                if (sex.value != SexState.FEMALE) {
                    if (isChecked) sex.value = SexState.FEMALE
                }
                else sex.value = SexState.NONE
            }
        }
    }



    fun btnLogin(id: String, pw: String) {
        Log.d("JKJK", "Login id:$id, pw:$pw")
    }

    fun btnSignUpNext() {
        Log.d("JKJK", "name=${inputName.value}, pw=${inputPw.value}, pwv=${inputPwv.value}, sex=${sex.value}")
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return InitViewModel(UserRepository.getInstance(application)!!) as T
        }
    }
}