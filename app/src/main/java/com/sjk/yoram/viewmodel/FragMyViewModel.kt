package com.sjk.yoram.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sjk.yoram.model.MyLoginData

class FragMyViewModel: ViewModel() {
    val myInfo = MutableLiveData<MyLoginData>()

    init {
        myInfo.value = MyLoginData()
    }

    fun getMyFullName(): String {
        val f = myInfo.value!!.fname
        val l = myInfo.value!!.lname
        return f + l
    }

    fun getMyDptName(): String {
        return myInfo.value!!.dptname
    }

    fun setMyInfo(newInfo: MyLoginData) {
        myInfo.value = newInfo
    }
}