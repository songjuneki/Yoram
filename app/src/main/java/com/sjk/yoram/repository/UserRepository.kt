package com.sjk.yoram.repository

import android.app.Application
import android.content.Context
import android.util.Log
import com.sjk.yoram.R
import com.sjk.yoram.model.*

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

    suspend fun userSignUp(newUser: NewUser): Int {
        return MyRetrofit.userApi.insert(newUser)
    }

    suspend fun loginUser(name: String, pw: String, bd: String = ""): LoginState {
        val id = getID(name, bd)

        return if (id == NAME_DUPLICATED) LoginState.NAME_SUCCESS_NEED_BD
        else if (id == NO_NAME) LoginState.NAME_FAIL
        else {
            if (isValidAccount(id, pw)){
                setLogin(id, pw)
                setIsInit(false)
                LoginState.LOGIN
            }
            else LoginState.PW_FAIL
        }
    }

    suspend fun isValidAccount(id: Int, pw: String): Boolean {
        Log.d("JKJK", "loginCheck id=$id, pw=$pw")
        return MyRetrofit.userApi.check(LoginCheck(id, pw))
    }

    suspend fun getCountByName(name: String): Int {
        val user = MyRetrofit.userApi.get(name)
        return user.size
    }

    suspend fun isSameName(name: String): Boolean {
        return MyRetrofit.userApi.get(name).size > 1
    }

    suspend fun getID(name: String, bd: String = ""): Int {
        var id = NO_NAME
        val user = MyRetrofit.userApi.get(name, bd)
        if (user.size == 1) id = user[0].id
        if (user.size > 1) id = NAME_DUPLICATED
        return id
    }

    suspend fun getLoginData(id: Int): MyLoginData {
        return MyRetrofit.userApi.getMyInfo(id)
    }



    companion object {
        private var instance: UserRepository? = null
        fun getInstance(application: Application): UserRepository? {
            if (instance == null) instance = UserRepository(application)
            return instance
        }

        private const val NAME_DUPLICATED = -10
        private const val NO_NAME = -1
    }
}
