package com.sjk.yoram.model.dto

import android.util.Log

data class UserPrivacyPolicy(
    var address: Boolean,
    var avatar: Boolean,
    var birth: Boolean,
    var name: Boolean,
    var phone: Boolean,
    var sex: Boolean,
    var tel: Boolean,
    var uid: Int
) {
    fun isEqual(pp: UserPrivacyPolicy?): Boolean {
        val flag = this.uid == pp!!.uid
                && this.address == pp!!.address
                && this.avatar == pp!!.avatar
                && this.birth == pp!!.birth
                && this.name == pp!!.name
                && this.phone == pp!!.phone
                && this.sex == pp!!.sex
                && this.tel == pp!!.tel

        if (pp == null) return false
        return flag
    }
}