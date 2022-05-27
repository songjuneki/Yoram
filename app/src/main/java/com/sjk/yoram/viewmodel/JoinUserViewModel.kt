package com.sjk.yoram.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class JoinUserViewModel: ViewModel() {
    val fname = MutableLiveData<String>()
    val lname = MutableLiveData<String>()
    val sex = MutableLiveData<Boolean>()
    val pw = MutableLiveData<String>()
    val pwValid = MutableLiveData<String>()
    val tel1 = MutableLiveData<String>()
    val tel2 = MutableLiveData<String>()
    val address = MutableLiveData<String>()
    val carno = MutableLiveData<String>()


    fun sexChanged(checked: Boolean) {
        sex.value = checked
    }

    fun showInfo() {

    }

}