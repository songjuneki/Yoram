package com.sjk.yoram.model

import androidx.lifecycle.MutableLiveData
import com.sjk.yoram.model.dto.UserDetail

class MutableUserLiveData : MutableLiveData<UserDetail>() {
    init {
        value = null
    }

    fun getDepartment() = value?.department ?: 0
    fun getDepartmentName() = value?.department_name ?: "성도"
    fun getPosition() = value?.position ?: 1050
    fun getPositionName() = value?.position_name ?: "성도"
    fun getPermission() = UserPermission.values()[value?.permission ?: 0]

    fun modifyDepartment(code: Int, name: String) {
        if (value == null)
            return
        val current = value
        current!!.department = code
        current!!.department_name = name
        value = current
    }

    fun modifyPosition(code: Int, name: String) {
        if (value == null)
            return
        val current = value
        current!!.position = code
        current!!.position_name = name
        value = current
    }

    fun modifyPermission(permission: UserPermission) {
        if (value == null)
            return
        val current = value
        current!!.permission = permission.ordinal
        value = current
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