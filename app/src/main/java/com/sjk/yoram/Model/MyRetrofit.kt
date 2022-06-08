package com.sjk.yoram.Model

import android.util.Log
import com.sjk.yoram.Model.dto.Department
import com.sjk.yoram.Model.dto.Position
import com.sjk.yoram.Model.dto.SimpleUser
import com.sjk.yoram.Model.dto.User
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

object MyRetrofit {
    private fun getRetrofit(): Retrofit {
        val okHttp: OkHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl("http://3.39.51.49:8080/api/")
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getMyApi(): MyApi {
        return getRetrofit().create(MyApi::class.java)
    }

    suspend fun checkServer(): Boolean {
        try {
            getMyApi().serverCheck()
        }catch(e: SocketTimeoutException){
            Log.d("JKJk", "retrofit exception : $e")
            return false
        } catch (e: ConnectException) {
            Log.d("JKJK", "retrofit exception : $e")
            return false
        }
        return true
    }
}

interface MyApi {
    @GET("hello")
    suspend fun serverCheck(): Response<HashMap<String, Any>>

    @GET("user/dpt/sp")
    suspend fun getSimpleUsersDepartment(@Query("dpt")dpt: Int): MutableList<SimpleUser>
    @GET("user/dpt")
    suspend fun getUsersDepartment(@Query("dpt")dpt: Int): MutableList<User>
    @GET("user/pos/sp")
    suspend fun getSimpleUsersPosition(@Query("pos")pos: Int): MutableList<SimpleUser>
    @GET("user/pos")
    suspend fun getUsersPosition(@Query("pos")pos: Int): MutableList<User>
    @GET("user/find")
    suspend fun getUserByName(@Query("name")name: String): MutableList<User>
    @GET("user/find")
    suspend fun getUserByNameAndBD(@Query("name")name: String, @Query("bd")bd: String): MutableList<User>
    @POST("user/new")
    suspend fun insertNewUser(@Body newUser: NewUser): Boolean

    @GET("user/name/all")
    suspend fun getAllSimpleUsersByName(): MutableList<SimpleUser>

    @GET("dpt/has")
    suspend fun getChildDepartments(@Query("parent")parent: Int): MutableList<Department>
    @GET("dpt/childs")
    suspend fun getAllChildDepartments(): MutableList<Department>
    @GET("dpt/tops")
    suspend fun getAllTopDepartments(): MutableList<Department>
    @GET("dpt")
    suspend fun loadDepartmentbyCode(@Query("code")code: Int): Department

    @GET("pos/parent")
    suspend fun getAllParentPositions(): MutableList<Position>
    @GET("pos/childs")
    suspend fun getAllChildPositions(): MutableList<Position>

    @POST("user/check")
    suspend fun checkMyUser(@Body checkData: LoginCheck): Boolean
    @GET("user/my")
    suspend fun getMyUserInfo(@Query("id")id: Int): MyLoginData

    @POST("user/upload/avatar")
    suspend fun uploadAvatar(@Part("pic")file:MultipartBody.Part, @Part("id")id: String): Response<Boolean>
    @GET("user/profile/url")
    suspend fun getAvatar(@Query("id")id: Int): String
}