package com.sjk.yoram.data.repository.retrofit.api

import com.sjk.yoram.data.entity.*
import com.sjk.yoram.presentation.common.model.LoginCheck
import com.sjk.yoram.presentation.common.model.NewUser
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import java.math.BigInteger

interface UserApi {
    // CRUD
    // Create
    @POST("user/new")
    suspend fun insert(@Body newUser: NewUser): Int
    @POST("give/add")
    suspend fun insertNewGive(@Body give: Give): Response<Boolean>

    // Read
    @GET("user/find")
    suspend fun findUser(@Query("name")name: String, @Query("bd")bd: String = ""): Int

    @POST("user/loginCheck")
    suspend fun check(@Body checkData: LoginCheck): Response<LoginResult>
    @GET("user/my")
    suspend fun getMyInfo(@Query("id")id: Int): MyLoginData
    @GET("user/permission")
    suspend fun getMyPermission(@Query("id")id: Int): Response<Int>

    @GET("give")
    suspend fun getUserGive(@Query("uid")uid: Int, @Query("year")year: Int = 0, @Query("month")month: Int = 0): Response<MutableList<Give>>
    @GET("give/amount")
    suspend fun getUserGiveAmount(@Query("uid")uid: Int, @Query("year")year: Int = 0, @Query("month")month: Int = 0, @Query("all")all: Boolean = false): Response<BigInteger>
    @GET("give/has/date")
    suspend fun getDatesHasGive(@Query("uid")uid: Int): Response<HashMap<String, List<Int>>>


    @GET("user/detail")
    suspend fun getUserDetail(@Query("id")id: Int, @Query("request")request: Int): UserDetail

    @GET("user/dpt")
    suspend fun getSimpleUsersDepartment(@Query("dpt")dpt: Int, @Query("request")request: Int): MutableList<SimpleUser>
    @GET("user/pos/sp")
    suspend fun getSimpleUsersPosition(@Query("pos")pos: Int, @Query("request")request: Int): MutableList<SimpleUser>
    @GET("user/pos")
    suspend fun getUsersPosition(@Query("pos")pos: Int, @Query("request")request: Int): MutableList<User>


    @GET("user/name/all")
    suspend fun getAllSimpleUsersByName(@Query("request")request: Int): MutableList<SimpleUser>

    @GET("user/profile/url")
    suspend fun getAvatar(@Query("id")id: Int): String

    @GET("user/attend")
    suspend fun getAttendList(@Query("id")id: Int, @Query("year")year: Int = 999, @Query("month")month: Int = 999): Response<MutableList<Attend>>

    @GET("user/pp")
    suspend fun getUserPrivacyPolicy(@Query("id")id: Int): UserPrivacyPolicy

    @POST("user/admin/new-user")
    suspend fun getNewUserList(@Body request: AdminInfo): Response<MutableList<NewUserForAdmin>>

    // UPDATE
    @Multipart
    @POST("user/avatar/init")
    suspend fun initAvatar(@Part("id")id: RequestBody): Response<Boolean>

    @Multipart
    @POST("user/avatar/upload")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part, @Part("id")id: RequestBody): Response<Boolean>

    @POST("user/detail/edit")
    suspend fun editUser(@Body info: UserDetail): Response<Boolean>

    @POST("user/verify")
    suspend fun attendUser(@Body attend: com.sjk.yoram.data.entity.Attend): Boolean

    @POST("user/pp/edit")
    suspend fun editUserPrivacyPolicy(@Body privacyPolicy: UserPrivacyPolicy): Boolean

    @POST("give/edit")
    suspend fun editGive(@Body give: Give): Response<Boolean>


    // DELETE
    @POST("give/del")
    suspend fun deleteGive(@Body give: Give): Response<Boolean>
    @POST("user/safe")
    suspend fun deleteUser(@Body info: com.sjk.yoram.data.entity.AccountDeleteInfo): Response<Boolean>
}