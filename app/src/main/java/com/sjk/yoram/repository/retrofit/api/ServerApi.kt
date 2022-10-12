package com.sjk.yoram.repository.retrofit.api

import com.sjk.yoram.model.dto.Banner
import com.sjk.yoram.model.dto.GiveType
import com.sjk.yoram.model.dto.Juso
import com.sjk.yoram.model.dto.WorshipType
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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
    suspend fun getBannerList(@Query("all")all: Boolean = false): Response<List<Banner>>

    @POST("banner/modify")
    suspend fun editBanners(@Body banners: List<Banner>): Response<List<Boolean>>

    @POST("banner/delete")
    suspend fun deleteBanner(@Body banner: Banner): Response<Boolean>

    @GET("get-max-week")
    suspend fun getMaxWeekOfMonth(@Query("year")year: Int = 0, @Query("month")month: Int= 0): Response<Int>

    @GET("ws/all")
    suspend fun getAllWorship(): List<WorshipType>

    @GET("give/type/all")
    suspend fun getAllGiveType(): Response<List<GiveType>>
}