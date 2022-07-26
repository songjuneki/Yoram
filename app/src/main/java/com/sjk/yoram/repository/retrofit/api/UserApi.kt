package com.sjk.yoram.repository.retrofit.api

import com.sjk.yoram.model.LoginCheck
import com.sjk.yoram.model.MyLoginData
import com.sjk.yoram.model.NewUser
import com.sjk.yoram.model.dto.Attend
import com.sjk.yoram.model.dto.SimpleUser
import com.sjk.yoram.model.dto.User
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

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


    @GET("user/dpt/sp")
    suspend fun getSimpleUsersDepartment(@Query("dpt")dpt: Int): MutableList<SimpleUser>
    @GET("user/dpt")
    suspend fun getUsersDepartment(@Query("dpt")dpt: Int): MutableList<User>
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