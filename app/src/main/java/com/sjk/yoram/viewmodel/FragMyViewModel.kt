package com.sjk.yoram.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sjk.yoram.model.dto.MyLoginData

class FragMyViewModel: ViewModel() {
    val myInfo = MutableLiveData<MyLoginData>()

    init {
        myInfo.value = MyLoginData()
    }

    fun getMyFullName(): String {
        return myInfo.value!!.name
    }

    fun getMyDptName(): String {
        return myInfo.value!!.department_name
    }

    fun setMyInfo(newInfo: MyLoginData) {
        myInfo.value = newInfo
    }
}