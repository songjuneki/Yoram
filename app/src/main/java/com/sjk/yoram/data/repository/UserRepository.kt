package com.sjk.yoram.data.repository

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toBitmapOrNull
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.github.sumimakito.awesomeqr.AwesomeQrRenderer
import com.github.sumimakito.awesomeqr.option.RenderOption
import com.github.sumimakito.awesomeqr.option.background.StillBackground
import com.github.sumimakito.awesomeqr.option.color.Color
import com.github.sumimakito.awesomeqr.option.logo.Logo
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.sjk.yoram.R
import com.sjk.yoram.data.entity.*
import com.sjk.yoram.presentation.common.model.GiveListItem
import com.sjk.yoram.presentation.common.model.LoginCheck
import com.sjk.yoram.presentation.common.model.LoginState
import com.sjk.yoram.presentation.common.model.NewUser
import com.sjk.yoram.data.repository.retrofit.MyRetrofit
import com.sjk.yoram.util.MySecurity
import com.sjk.yoram.util.toHex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.math.BigInteger
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class UserRepository(private val application: Application) {
    private val sharedPref = application.getSharedPreferences(application.getString(R.string.YORAM_LOCAL_PREF), Context.MODE_PRIVATE)

    fun getIsInit(): Boolean = sharedPref.getBoolean(application.getString(R.string.YORAM_LOCAL_PREF_ISINIT), true)
    fun setIsInit(boolean: Boolean) {
        sharedPref.edit().putBoolean(application.getString(R.string.YORAM_LOCAL_PREF_ISINIT), boolean).apply()
    }
    fun getLoginID() = sharedPref.getInt(application.getString(R.string.YORAM_LOCAL_PREF_MYID), -1)
    fun getLoginPW() = sharedPref.getString(application.getString(R.string.YORAM_LOCAL_PREF_MYPW), "@")
    fun setLogin(id: Int, pw: String) {
        sharedPref.edit().putInt(application.getString(R.string.YORAM_LOCAL_PREF_MYID), id).putString(application.getString(R.string.YORAM_LOCAL_PREF_MYPW), pw).apply()
    }

    suspend fun userSignUp(newUser: NewUser): Int {
        return MyRetrofit.userApi.insert(newUser)
    }

    suspend fun loginUser(name: String, pw: String, bd: String = ""): LoginState {
        val id = findUserId(name, bd)

        if (id == NO_NAME) return LoginState.NAME_FAIL
        if (id == NAME_DUPLICATED) return LoginState.NAME_SUCCESS_NEED_BD

        val result = MyRetrofit.userApi.check(LoginCheck(id, name, pw, bd))

        if (!result.isSuccessful)
            return LoginState.NETWORK_ERROR


        return when (result.body()) {
            null -> LoginState.NETWORK_ERROR
            LoginResult.NAME_FAIL -> LoginState.NAME_FAIL
            LoginResult.PW_FAIL -> LoginState.PW_FAIL
            LoginResult.NAME_OK_NEED_BD -> LoginState.NAME_SUCCESS_NEED_BD
            LoginResult.BD_FAIL -> LoginState.BD_FAIL
            LoginResult.NAME_OK_BD_OK_PW_FAIL -> LoginState.NAME_BD_OK_PW_FAIL
            LoginResult.LOGIN ->  {
                setLogin(id, pw)
                setIsInit(false)
                LoginState.LOGIN
            }
        }
    }

    suspend fun isValidAccount(id: Int, name: String, pw: String, bd: String): Boolean {
        val result = MyRetrofit.userApi.check(
            LoginCheck(id, name, pw, bd)
        )

        if (!result.isSuccessful)
            return false

        return when (result.body()) {
            LoginResult.LOGIN -> true
            else -> false
        }
    }

    suspend fun getMyPermission(id: Int): Int {
        if (id < 0) return 0
        val response = MyRetrofit.userApi.getMyPermission(id)
        if (response.isSuccessful)
            return response.body()!!
        return 0
    }


    suspend fun findUserId(name: String, bd: String = ""): Int {
        return MyRetrofit.userApi.findUser(name, bd)
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

    @SuppressLint("UseCompatLoadingForDrawables")
    suspend fun getAvatarBitmap(id: Int = getLoginID()): Bitmap? {
        val url = MyRetrofit.userApi.getAvatar(id)
        val loader = ImageLoader(application.applicationContext)
        val req = ImageRequest.Builder(application.applicationContext).data(url)
            .allowHardware(false)
            .build()
        return try {
            val result = (loader.execute(req) as SuccessResult).drawable
            result.toBitmap()
        } catch (e: Exception) {
            BitmapFactory.decodeResource(application.applicationContext.resources, R.drawable.ic_avatar)
                ?: application.applicationContext.resources.getDrawable(R.drawable.ic_avatar, application.theme).toBitmapOrNull()
        }
    }

    suspend fun uploadAvatar(img: Bitmap?): Boolean {
        if (img == null) {
            val body = getLoginID().toString().toRequestBody("text/plain".toMediaTypeOrNull())
            MyRetrofit.userApi.initAvatar(body)
            return true
        }
        val storageDir: File? = application.applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = withContext(Dispatchers.IO) {
            File.createTempFile("JPEG_UPLOAD_TEMP", ".jpg", storageDir)
        }
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


    suspend fun attendUser(attend: com.sjk.yoram.data.entity.Attend): Boolean {
        return MyRetrofit.userApi.attendUser(attend)
    }

    suspend fun getAttendList(id: Int, year: Int = 999, month: Int = 999): List<com.sjk.yoram.data.entity.Attend> {
        val list = MyRetrofit.userApi.getAttendList(id, year, month)
        if (list.isSuccessful)
            return list.body()!!
        return listOf()
    }

    fun getUserCode(special: Boolean = false): Bitmap {
        val option = makeCodeOption(special)
        val result = AwesomeQrRenderer.render(option)
        return if (result.bitmap != null)
            result.bitmap!!
        else
            AwesomeQrRenderer.render(makeFailureCode("cannot make code")).bitmap!!
    }

    fun getNotValidUserCode(): Bitmap {
        return application.getDrawable(R.drawable.ic_blured_code)?.toBitmap()
            ?: return AwesomeQrRenderer.render(makeFailureCode("cannot get blurred code")).bitmap!!
    }


    private fun makeCodeOption(special: Boolean = false) = RenderOption().apply {
        val date = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.KOREA)
        val p = "GUSEONGCHURCH;BY.SONGJUNEKI;DATE:${dateFormat.format(date)};TIME:${timeFormat.format(date)};ID:${getLoginID()}"

        content = MySecurity().encodeBase64(p.toByteArray().toHex())
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

        if (special) {
            background = StillBackground(
                alpha = 1.0f,
                bitmap = BitmapFactory.decodeResource(application.resources, R.raw.cat)
            )
            color = color.apply {
                dark = 0xFFFFFFFF.toInt()
                light = 0x00000000.toInt()
            }
            logo = null
        }
    }

    private fun makeFailureCode(error: String) = RenderOption().apply {
        val p = "GUSEONGCHURCH;BY.SONGJUNEKI;ERROR:${error}"
        content = MySecurity().encodeBase64(p.toByteArray().toHex())
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

    suspend fun getNewUserList(request: com.sjk.yoram.data.entity.AdminInfo): MutableList<NewUserForAdmin> {
        val res = MyRetrofit.userApi.getNewUserList(request)
        if (!res.isSuccessful)
            return mutableListOf()
        return res.body() ?: mutableListOf()
    }

    suspend fun requestDeleteUser(info: com.sjk.yoram.data.entity.AccountDeleteInfo): Boolean {
        val res = MyRetrofit.userApi.deleteUser(info)
        if (!res.isSuccessful)
            return false
        return res.body() ?: false
    }

    suspend fun getRequestUser(): RequestUser = RequestUser(getLoginID(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), getMyPermission(getLoginID()))

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
