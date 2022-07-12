package com.sjk.yoram.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.sjk.yoram.R
import com.sjk.yoram.model.*
import com.sjk.yoram.repository.ServerRepository
import com.sjk.yoram.repository.UserRepository

class MainViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _currentFragmentType = MutableLiveData(FragmentType.Fragment_HOME)
    val currentFragmentType: LiveData<FragmentType> = _currentFragmentType
    val dptClickState = MutableLiveData<Boolean>(false)
    val loginState = MutableLiveData(LoginState.NONE)
    val loginData = MutableLiveData<MyLoginData>()

    ///// new
    private val _currentFragment = MutableLiveData<Event<FragmentType>>()
    val currentFragment: LiveData<Event<FragmentType>>
        get() = _currentFragment


    // TODO : 같은 네비 클릭시 오류 처리
    fun naviItemClicked(menuItemId: Int): Boolean {
        when (menuItemId) {
            R.id.navi_home -> {
                _currentFragment.value = Event(FragmentType.Fragment_HOME)
            }
            R.id.navi_dptment -> {
                if (currentFragment.value == Event(FragmentType.Fragment_DPTMENT)) return false
                _currentFragment.value = Event(FragmentType.Fragment_DPTMENT)
            }
            R.id.navi_id -> _currentFragment.value = Event(FragmentType.Fragment_ID)
            R.id.navi_board -> _currentFragment.value = Event(FragmentType.Fragment_BOARD)
            R.id.navi_my -> _currentFragment.value = Event(FragmentType.Fragment_MY)
            else -> throw IllegalArgumentException("not found menu item id")
        }
        return true
    }


    //// new end


    fun setCurrentFragment(menuItemId: Int): Boolean {
        val fragType = getFragmentType(menuItemId)
        changeCurrentFragment(fragType)
        return true
    }

    fun moveDptFrag() {
        changeCurrentFragment(FragmentType.Fragment_DPTMENT)
        dptClickState.value = !dptClickState.value!!
    }


    private fun getFragmentType(menuItemId: Int): FragmentType {
        return when(menuItemId) {
            R.id.navi_home -> FragmentType.Fragment_HOME
            R.id.navi_dptment -> FragmentType.Fragment_DPTMENT
            R.id.navi_id -> FragmentType.Fragment_ID
            R.id.navi_board -> FragmentType.Fragment_BOARD
            R.id.navi_my -> FragmentType.Fragment_MY
            else -> throw IllegalArgumentException("not found menu item id")
        }
    }

    private fun changeCurrentFragment(fragmentType: FragmentType) {
        if (currentFragmentType.value == fragmentType) return

        _currentFragmentType.value = fragmentType
    }

    fun getUserPermission(): UserPermission {
        val data = loginData.value ?: MyLoginData()
        Log.d("JKJK", "myPermission=${data.permission}")
        return UserPermission.values()[data.permission]
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(UserRepository.getInstance(application)!!) as T
        }
    }
}