package com.sjk.yoram.repository.retrofit.api

import com.sjk.yoram.model.dto.Board
import com.sjk.yoram.model.dto.BoardCategory
import com.sjk.yoram.model.dto.ReservedBoardCategory
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BoardApi {
    @GET("web/category/list")
    suspend fun getAllBoardCategoryList(): Response<MutableList<BoardCategory>>

    @GET("board/list")
    suspend fun getReservedBoardCategoryList(): Response<MutableList<ReservedBoardCategory>>

    @POST("web/board")
    suspend fun getPagedBoardListByCategory(@Body category: BoardCategory, @Query("page") page: Int = 0): Response<MutableList<Board>>
}