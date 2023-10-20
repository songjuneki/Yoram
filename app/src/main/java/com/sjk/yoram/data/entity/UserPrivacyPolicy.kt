package com.sjk.yoram.data.entity

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
        if (pp == null) return false

        return (uid == pp.uid
                && address == pp.address
                && avatar == pp.avatar
                && birth == pp.birth
                && name == pp.name
                && phone == pp.phone
                && sex == pp.sex
                && tel == pp.tel)
    }
}