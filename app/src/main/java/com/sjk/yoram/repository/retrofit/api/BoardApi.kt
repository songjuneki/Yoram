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
    @GET("board/category/reserved/list")
    suspend fun getReservedBoardCategoryList(): Response<MutableList<ReservedBoardCategory>>

    @GET("board/category/inReserveList")
    suspend fun getInReserveBoardCategoryList(): Response<List<BoardCategory>>

    @POST("board")
    suspend fun getPagedBoardListByCategory(@Body category: BoardCategory, @Query("page") page: Int = 0): Response<MutableList<Board>>

    @POST("board/admin/cat/upload")
    suspend fun postReserveBoardCategoryList(@Body reserveBoardCategoryList: List<ReservedBoardCategory>): Response<Boolean>

}