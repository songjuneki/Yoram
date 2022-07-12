package com.sjk.yoram.repository.retrofit.api

import com.sjk.yoram.model.dto.Banner
import com.sjk.yoram.model.dto.WorshipType
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ServerApi {
    @GET("hello")
    suspend fun serverCheck(): Response<HashMap<String, Any>>

    @GET("ws/all")
    suspend fun getAllWorship(): Response<List<WorshipType>>

    @GET("banner")
    suspend fun getBannerInfo(@Query("id")id: Int, @Query("detail")detail: Boolean): Banner

    @GET("banner/count")
    suspend fun getAllBannerCount(): Response<Int>

    @GET("banner/list")
    suspend fun getBannerList(): Response<List<Int>>
}