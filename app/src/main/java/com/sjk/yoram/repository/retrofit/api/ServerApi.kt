package com.sjk.yoram.repository.retrofit.api

import com.sjk.yoram.model.dto.Banner
import com.sjk.yoram.model.dto.Juso
import com.sjk.yoram.model.dto.WorshipType
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ServerApi {
    @GET("hello")
    suspend fun serverCheck(): Response<HashMap<String, Any>>

    @GET("juso")
    suspend fun searchAddress(@Query("keyword")keyword: String, @Query("page")page: Int = 1): Response<List<Juso>>

    @GET("banner")
    suspend fun getBannerInfo(@Query("id")id: Int, @Query("detail")detail: Boolean): Banner

    @GET("banner/count")
    suspend fun getAllBannerCount(): Response<Int>

    @GET("banner/list")
    suspend fun getBannerList(): Response<List<Int>>

    @GET("get-max-week")
    suspend fun getMaxWeekOfMonth(@Query("year")year: Int = 0, @Query("month")month: Int= 0): Response<Int>

    @GET("ws/all")
    suspend fun getAllWorship(): List<WorshipType>
}