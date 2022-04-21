package com.sjk.yoram.Model

import com.sjk.yoram.Model.dto.Department
import com.sjk.yoram.Model.dto.User
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object MyRetrofit {
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://3.39.51.49:8080/api/db/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getMyApi(): MyApi {
        return getRetrofit().create(MyApi::class.java)
    }
}

interface MyApi {
    @GET("user/all")
    suspend fun getAllUsers(): MutableList<User>
    @GET("user/dpt")
    suspend fun getUsersDepartment(@Query("dpt")dpt: Int): MutableList<User>
    @GET("dpt/all")
    suspend fun getAllDepartments(): MutableList<Department>
    @GET("dpt/has")
    suspend fun getChildDepartments(@Query("parent")parent: Int): MutableList<Department>
    @GET("dpt/childs")
    suspend fun getAllChildDepartments(): MutableList<Department>
    @GET("dpt/tops")
    suspend fun getAllTopDepartments(): MutableList<Department>
}