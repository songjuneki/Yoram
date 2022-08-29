package com.sjk.yoram.repository

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toBitmapOrNull
import com.github.sumimakito.awesomeqr.AwesomeQrRenderer
import com.github.sumimakito.awesomeqr.option.RenderOption
import com.github.sumimakito.awesomeqr.option.color.Color
import com.github.sumimakito.awesomeqr.option.logo.Logo
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.sjk.yoram.R
import com.sjk.yoram.model.*
import com.sjk.yoram.model.dto.Attend
import com.sjk.yoram.model.dto.MyLoginData
import com.sjk.yoram.model.dto.UserDetail
import java.math.BigInteger
import java.text.SimpleDateFormat

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
                return LoginState.LOGIN
            }
            else return LoginState.PW_FAIL
        }
    }

    suspend fun isValidAccount(id: Int, pw: String): Boolean {
        Log.d("JKJK", "loginCheck id=$id, pw=$pw")
        return MyRetrofit.userApi.check(LoginCheck(id, pw))
    }

    suspend fun getMyPermission(id: Int): Int {
        val response = MyRetrofit.userApi.getMyPermission(id)
        Log.d("JKKJK", "permission = ${response.body()!!}")
        if (response.isSuccessful)
            return response.body()!!
        return -11
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

    suspend fun getCurrentMonthGiveAmount(id: Int): BigInteger {
        val result = MyRetrofit.userApi.getUserGiveAmount(id)
        return BigInteger(result)
    }

    suspend fun getUserDetail(id: Int): UserDetail = MyRetrofit.userApi.getUserDetail(id)

    suspend fun attendUser(attend: Attend): Boolean {
        return MyRetrofit.userApi.attendUser(attend)
    }

    suspend fun getUserCode(): Bitmap {
        val option = makeCodeOption(getLoginData(getLoginID()))
        val result = AwesomeQrRenderer.render(option)
        return if (result.bitmap != null)
            result.bitmap!!
        else
            AwesomeQrRenderer.render(makeFailureCode("cannot make code")).bitmap!!
    }

    fun getNotValidUserCode(): Bitmap {
        return application.getDrawable(R.drawable.ic_blured_code)?.toBitmap()
            ?: return AwesomeQrRenderer.render(makeFailureCode("cannot get blured code")).bitmap!!
    }

    private fun makeCodeOption(user: MyLoginData, special: Boolean = false) = RenderOption().apply {
        val date = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val timeFormat = SimpleDateFormat("HHmmss")
        val p = "GUSEONGCHURCH;BY.SONGJUNEKI;DATE:${dateFormat.format(date)};TIME:${timeFormat.format(date)};ID:${user.id}"
        content = AESUtil().Encrypt(p)
        size = 1000
        borderWidth = 10
        ecl = ErrorCorrectionLevel.M
        patternScale = 0.7f
        roundedPatterns = false
        clearBorder = true

        color = Color().apply {
            background = android.graphics.Color.TRANSPARENT
            dark = android.graphics.Color.BLACK
            light = android.graphics.Color.WHITE
            auto = false
        }

        logo = Logo().apply {
            bitmap = application.getDrawable(R.drawable.icon_temp)?.toBitmapOrNull()
            this.scale = 0.3f
        }
    }

    private fun makeFailureCode(error: String) = RenderOption().apply {
        val p = "GUSEONGCHURCH;BY.SONGJUNEKI;ERROR:${error}"
        content = AESUtil().Encrypt(p)
        size = 1000
        borderWidth = 10
        ecl = ErrorCorrectionLevel.M
        patternScale = 0.7f
        roundedPatterns = false
        clearBorder = true

        color = Color().apply {
            background = android.graphics.Color.TRANSPARENT
            dark = android.graphics.Color.BLACK
            light = android.graphics.Color.WHITE
            auto = false
        }

        logo = Logo().apply {
            bitmap = application.getDrawable(R.drawable.icon_temp)?.toBitmapOrNull()
            this.scale = 0.3f
        }
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
