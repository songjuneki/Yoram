package com.sjk.yoram.repository.retrofit.api

import com.sjk.yoram.model.dto.Banner
import com.sjk.yoram.model.dto.GiveType
import com.sjk.yoram.model.dto.Juso
import com.sjk.yoram.model.dto.WorshipType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

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

    @Multipart
    @POST("banner/upload")
    suspend fun uploadNewBanner(@Part file: MultipartBody.Part, @Part("id")ownerId: RequestBody, @Part("date")uploadDate: RequestBody, @Part("time")uploadTime: RequestBody): Response<Boolean>

    @GET("get-max-week")
    suspend fun getMaxWeekOfMonth(@Query("year")year: Int = 0, @Query("month")month: Int= 0): Response<Int>

    @GET("ws/all")
    suspend fun getAllWorship(): List<WorshipType>

    @GET("give/type/all")
    suspend fun getAllGiveType(): Response<List<GiveType>>
}