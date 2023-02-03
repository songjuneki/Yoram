package com.sjk.yoram.repository

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toBitmapOrNull
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.github.sumimakito.awesomeqr.AwesomeQrRenderer
import com.github.sumimakito.awesomeqr.option.RenderOption
import com.github.sumimakito.awesomeqr.option.color.Color
import com.github.sumimakito.awesomeqr.option.logo.Logo
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.sjk.yoram.R
import com.sjk.yoram.model.*
import com.sjk.yoram.model.dto.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.math.BigInteger
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
        return MyRetrofit.userApi.check(LoginCheck(id, pw))
    }

    suspend fun getMyPermission(id: Int): Int {
        val response = MyRetrofit.userApi.getMyPermission(id)
        if (response.isSuccessful)
            return response.body()!!
        return 0
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
        if (!result.isSuccessful)
            return BigInteger.ZERO
        return result.body() ?: BigInteger.ZERO
    }

    suspend fun getUserDetail(id: Int): UserDetail = MyRetrofit.userApi.getUserDetail(id, getLoginID())

    suspend fun getAvatarBitmap(id: Int = getLoginID()): Bitmap {
        val url = MyRetrofit.userApi.getAvatar(id)
        val loader = ImageLoader(application.applicationContext)
        val req = ImageRequest.Builder(application.applicationContext).data(url)
            .allowHardware(false)
            .build()
        val result = (loader.execute(req) as SuccessResult).drawable
        return result.toBitmap()
    }

    suspend fun uploadAvatar(img: Bitmap?): Boolean {
        if (img == null) {
            val body = getLoginID().toString().toRequestBody("text/plain".toMediaTypeOrNull())
            MyRetrofit.userApi.initAvatar(body)
            return true
        }
        val storageDir: File? = application.applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("JPEG_UPLOAD_TEMP", ".jpg", storageDir)
        var out: OutputStream? = null

        try {
            out = FileOutputStream(file)
            img.compress(Bitmap.CompressFormat.JPEG, 100, out)
        } catch (e: Exception){
            e.printStackTrace()
            out?.close()
            return false
        }
        out?.close()

        val requestBody = MultipartBody.Part.createFormData("pic", "avatar.jpg", file.asRequestBody("image/*".toMediaType()))
        val upload = MyRetrofit.userApi.uploadAvatar(requestBody, getLoginID().toString().toRequestBody("text/plain".toMediaTypeOrNull()))
        return upload.isSuccessful
    }

    suspend fun editUserInfo(info: UserDetail): Boolean {
        val post = MyRetrofit.userApi.editUser(info)
        if (post.isSuccessful)
            return true
        return false
    }


    suspend fun attendUser(attend: Attend): Boolean {
        return MyRetrofit.userApi.attendUser(attend)
    }

    suspend fun getAttendList(id: Int, year: Int = 999, month: Int = 999): List<Attend> {
        val list = MyRetrofit.userApi.getAttendList(id, year, month)
        if (list.isSuccessful)
            return list.body()!!
        return listOf()
    }

    fun getUserCode(): Bitmap {
        val option = makeCodeOption()
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

    private fun makeCodeOption(special: Boolean = false) = RenderOption().apply {
        val date = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val timeFormat = SimpleDateFormat("HH:mm:ss")
        val p = "GUSEONGCHURCH;BY.SONGJUNEKI;DATE:${dateFormat.format(date)};TIME:${timeFormat.format(date)};ID:${getLoginID()}"
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

    suspend fun getUserGiveAmount(id: Int, date: LocalDate): String {
        val res = MyRetrofit.userApi.getUserGiveAmount(id, date.year, date.monthValue)
        if (!res.isSuccessful)
            return "0"
        val format = DecimalFormat("###,###")
        return format.format(res.body()!!.toString())
    }

    suspend fun getAllUserGiveAmount(id: Int): String {
        val res = MyRetrofit.userApi.getUserGiveAmount(uid = id, all = true)
        if (!res.isSuccessful)
            return "0"
        val format = DecimalFormat("###,###")
        return format.format(res.body()!!.toString())
    }

    suspend fun getUserGiveList(id: Int, date: LocalDate): MutableList<GiveListItem> {
        val res = MyRetrofit.userApi.getUserGive(id, date.year, date.monthValue)
        if (!res.isSuccessful)
            return mutableListOf()
        val list = res.body()
        if (list.isNullOrEmpty())
            return mutableListOf()

        val items = mutableListOf<GiveListItem>()
        list.forEach {
            val decFormat = DecimalFormat("###,###")
            val date = LocalDate.parse(it.date).format(DateTimeFormatter.ofPattern("yy.MM.dd"))
            items.add(GiveListItem(it.name, date, decFormat.format(it.amount)))
        }
        return items
    }

    suspend fun getRawUserGiveList(id: Int, date: LocalDate): MutableList<Give> {
        val res = MyRetrofit.userApi.getUserGive(id, date.year, date.monthValue)
        if (!res.isSuccessful)
            return mutableListOf()
        return res.body() ?: mutableListOf()
    }

    suspend fun getDateListHasGive(uid: Int): HashMap<String, List<Int>> {
        val res = MyRetrofit.userApi.getDatesHasGive(uid)
        if (!res.isSuccessful)
            return hashMapOf()
        val map = res.body()
        return map ?: hashMapOf()
    }

    suspend fun insertNewGive(newGive: Give): Boolean {
        val res = MyRetrofit.userApi.insertNewGive(newGive)
        if (!res.isSuccessful)
            return false
        return res.body() ?: false
    }

    suspend fun editGive(give: Give): Boolean {
        val res = MyRetrofit.userApi.editGive(give)
        if (!res.isSuccessful)
            return false
        return res.body() ?: false
    }

    suspend fun deleteGive(give: Give): Boolean {
        val res = MyRetrofit.userApi.deleteGive(give)
        if (!res.isSuccessful)
            return false
        return res.body() ?: false
    }

    suspend fun getUserPrivacyPolicy(id: Int = getLoginID()): UserPrivacyPolicy {
        return MyRetrofit.userApi.getUserPrivacyPolicy(id)
    }

    suspend fun editUserPrivacyPolicy(pp: UserPrivacyPolicy): Boolean {
        return MyRetrofit.userApi.editUserPrivacyPolicy(pp)
    }

    suspend fun getNewUserList(request: AdminInfo): MutableList<NewUserForAdmin> {
        val res = MyRetrofit.userApi.getNewUserList(request)
        if (!res.isSuccessful)
            return mutableListOf()
        return res.body() ?: mutableListOf()
    }

    suspend fun requestDeleteUser(info: AccountDeleteInfo): Boolean {
        val res = MyRetrofit.userApi.deleteUser(info)
        if (!res.isSuccessful)
            return false
        return res.body() ?: false
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
