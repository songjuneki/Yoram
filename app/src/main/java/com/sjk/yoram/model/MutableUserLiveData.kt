package com.sjk.yoram.model

import androidx.lifecycle.MutableLiveData
import com.sjk.yoram.model.dto.UserDetail

class MutableUserLiveData : MutableLiveData<UserDetail>() {
    init {
        value = null
    }

    fun modifyBirth(bd: String) {
        if (value == null)
            return
        val current = value
        current!!.birth = bd
        value = current
    }

    fun modifyPhone(phone: String) {
        if (value == null)
            return
        val current = value
        current!!.phone = phone
        value = current
    }

    fun modifyTel(tel: String) {
        if (value == null)
            return
        val current = value
        current!!.tel = tel
        value = current
    }

    fun modifyAddr(addr: String) {
        if (value == null)
            return
        val current = value
        current!!.address = addr
        value = current
    }

    fun modifyAddrm(more: String) {
        if (value == null)
            return
        val current = value
        current!!.address_more = more
        value = current
    }

    fun modifyCar(car: String) {
        if (value == null)
            return
        val current = value
        current!!.car = car
        value = current
    }

    fun notifyChange() {
        val current = value
        value = current
    }
}