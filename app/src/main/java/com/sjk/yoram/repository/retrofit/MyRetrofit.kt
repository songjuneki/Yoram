package com.sjk.yoram.model

import android.util.Log
import com.sjk.yoram.model.dto.*
import com.sjk.yoram.model.dto.Department
import com.sjk.yoram.repository.retrofit.api.ServerApi
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

object MyRetrofit {
    const val SERVER = "http://3.39.51.49:8080/api/"

    private fun getRetrofit(): Retrofit {
        val okHttp: OkHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(SERVER)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val serverApi: ServerApi by lazy { getRetrofit().create(ServerApi::class.java) }



    fun getMyApi(): MyApi {
        return getRetrofit().create(MyApi::class.java)
    }

    suspend fun checkServer(): Boolean {
        try {
            serverApi.serverCheck()
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

    @GET("ws/all")
    suspend fun getAllWorship(): List<WorshipType>

    @POST("user/verify")
    suspend fun attendUser(@Body attend: Attend): Boolean
}