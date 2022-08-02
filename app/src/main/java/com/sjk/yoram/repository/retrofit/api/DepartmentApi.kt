package com.sjk.yoram.repository.retrofit.api

import com.sjk.yoram.model.dto.*
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DepartmentApi {

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
}