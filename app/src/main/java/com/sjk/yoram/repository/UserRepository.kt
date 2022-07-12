package com.sjk.yoram.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.sjk.yoram.R
import com.sjk.yoram.model.LoginCheck
import com.sjk.yoram.model.MyRetrofit

class UserRepository(private val application: Application) {
    private val sharedPref = application.getSharedPreferences(application.getString(R.string.YORAM_LOCAL_PREF), Context.MODE_PRIVATE)

    fun getIsInit(): Boolean = sharedPref.getBoolean(application.getString(R.string.YORAM_LOCAL_PREF_ISINIT), true)
    fun setIsInit(boolean: Boolean) {
        sharedPref.edit().putBoolean(application.getString(R.string.YORAM_LOCAL_PREF_ISINIT), boolean).commit()
    }
    fun getLoginID() = sharedPref.getInt(application.getString(R.string.YORAM_LOCAL_PREF_MYID), -1)
    fun getLoginPW() = sharedPref.getString(application.getString(R.string.YORAM_LOCAL_PREF_MYPW), "@")
    fun setLogin(id: Int, pw: String) {
        sharedPref.edit().putInt(application.getString(R.string.YORAM_LOCAL_PREF_MYID), id).putString(application.getString(R.string.YORAM_LOCAL_PREF_MYPW), pw).commit()
    }

    suspend fun isValidAccount(id: Int, pw: String): Boolean {
        return MyRetrofit.getMyApi().checkMyUser(LoginCheck(id, pw))
    }

    suspend fun getCountByName(name: String): Int {
        val user = MyRetrofit.getMyApi().getUserByName(name)
        return user.size
    }

    suspend fun isSameName(name: String): Boolean {
        return MyRetrofit.getMyApi().getUserByName(name).size > 1
    }

    suspend fun getIDFromName(name: String): Int {
        val user = MyRetrofit.getMyApi().getUserByName(name)
        var id: Int = -1
        if (user.size == 1) id = user[0].id
        return id
    }



    companion object {
        private var instance: UserRepository? = null
        fun getInstance(application: Application): UserRepository? {
            if (instance == null) instance = UserRepository(application)
            return instance
        }
    }
}
