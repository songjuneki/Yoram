package com.sjk.yoram.data.repository.retrofit.api

import com.sjk.yoram.data.entity.Department
import com.sjk.yoram.data.entity.Position
import com.sjk.yoram.data.entity.RequestUser
import com.sjk.yoram.presentation.main.department.DepartmentNode
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DepartmentApi {
    @GET("dpt/has")
    suspend fun getChildDepartments(@Query("parent")parent: Int): MutableList<Department>
    @GET("dpt/tops")
    suspend fun getAllTopDepartments(): MutableList<Department>

    @POST("dpt/name")
    suspend fun getDepartmentNodeListByName(@Body request: RequestUser): Response<MutableList<DepartmentNode>>
    @POST("dpt/dpt")
    suspend fun getDepartmentNodeListByDepartment(@Body request: RequestUser): Response<MutableList<DepartmentNode>>

    @POST("dpt/pos")
    suspend fun getDepartmentNodeListByPosition(@Body request: RequestUser): Response<MutableList<DepartmentNode>>

    @GET("dpt/all")
    suspend fun getAllDepartmentList(): Response<MutableList<Department>>
    @GET("dpt")
    suspend fun loadDepartmentbyCode(@Query("code")code: Int): Department

    @GET("pos/all")
    suspend fun getAllPositions(): Response<MutableList<Position>>
    @GET("pos/parent")
    suspend fun getAllTopPositions(): MutableList<Position>
    @GET("pos/child")
    suspend fun getChildPosition(@Query("parent")parent: Int): MutableList<Position>

    @GET("pos/childs")
    suspend fun getAllChildPositions(): MutableList<Position>
    @GET("pos")
    suspend fun getPosition(@Query("code")code: Int): Position

    @GET("dpt/edit/check")
    suspend fun getCheckDepartmentIsUsing(@Query("code")code: Int): Response<Boolean>
    @POST("dpt/edit")
    suspend fun uploadDepartmentList(@Body departmentList: List<Department>): Response<Boolean>

    @GET("pos/edit/check")
    suspend fun getCheckPositionIsUsing(@Query("code")code: Int): Response<Boolean>
    @POST("pos/edit")
    suspend fun uploadPositionList(@Body posList: List<Position>): Response<Boolean>
}