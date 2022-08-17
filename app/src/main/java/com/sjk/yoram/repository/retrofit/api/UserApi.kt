package com.sjk.yoram.repository.retrofit.api

import androidx.annotation.Nullable
import com.sjk.yoram.model.LoginCheck
import com.sjk.yoram.model.NewUser
import com.sjk.yoram.model.dto.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import java.math.BigInteger

interface UserApi {
    // CRUD
    // Create
    @POST("user/new")
    suspend fun insert(@Body newUser: NewUser): Int

    // Read
    @GET("user/find")
    suspend fun get(@Query("name")name: String, @Query("bd")bd: String = ""): MutableList<User>
    @POST("user/check")
    suspend fun check(@Body checkData: LoginCheck): Boolean
    @GET("user/my")
    suspend fun getMyInfo(@Query("id")id: Int): MyLoginData
    @GET("user/permission")
    suspend fun getMyPermission(@Query("id")id: Int): Response<Int>

    @GET("give")
    suspend fun getUserGive(@Query("uid")uid: Int, @Query("year")year: Int = 0, @Query("month")month: Int = 0): ArrayList<Give>
    @GET("give/amount")
    suspend fun getUserGiveAmount(@Query("uid")uid: Int, @Query("year")year: Int = 0, @Query("month")month: Int = 0): String


    @GET("user/detail")
    suspend fun getUserDetail(@Query("id")id: Int): UserDetail
    @GET("user/dpt")
    suspend fun getSimpleUsersDepartment(@Query("dpt")dpt: Int): MutableList<SimpleUser>
    @GET("user/pos/sp")
    suspend fun getSimpleUsersPosition(@Query("pos")pos: Int): MutableList<SimpleUser>
    @GET("user/pos")
    suspend fun getUsersPosition(@Query("pos")pos: Int): MutableList<User>


    @GET("user/name/all")
    suspend fun getAllSimpleUsersByName(): MutableList<SimpleUser>




    @POST("user/upload/avatar")
    suspend fun uploadAvatar(@Part("pic")file: MultipartBody.Part, @Part("id")id: String): Response<Boolean>
    @GET("user/profile/url")
    suspend fun getAvatar(@Query("id")id: Int): String

    @POST("user/verify")
    suspend fun attendUser(@Body attend: Attend): Boolean
}