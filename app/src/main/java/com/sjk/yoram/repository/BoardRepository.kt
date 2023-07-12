package com.sjk.yoram.repository

import android.app.Application
import android.util.Log
import com.sjk.yoram.model.ApiState
import com.sjk.yoram.model.dto.Board
import com.sjk.yoram.model.dto.BoardCategory
import com.sjk.yoram.model.dto.ReservedBoardCategory
import com.sjk.yoram.repository.retrofit.MyRetrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class BoardRepository(private val application: Application) {
    private val api = MyRetrofit.boardApi

    suspend fun getAllCategoryList(): Flow<ApiState<List<BoardCategory>>> = flow {
        val response = api.getAllBoardCategoryList()
        if (!response.isSuccessful) {
            emit(ApiState.Error("Response unsuccessful"))
            return@flow
        }
        emit(ApiState.Success(response.body()?.toList() ?: listOf()))
    }.flowOn(Dispatchers.IO)
        .onStart { emit(ApiState.Loading()) }
        .catch { emit(ApiState.Error(it.message ?: "Cause unknown error")) }

    suspend fun getReservedCategoryList(): Flow<ApiState<List<ReservedBoardCategory>>> = flow {
        val response = api.getReservedBoardCategoryList()
        if (!response.isSuccessful) {
            emit(ApiState.Error("Response unsuccessful"))
        }
        emit(ApiState.Success(response.body()?.toList() ?: listOf()))
    }.onStart { emit(ApiState.Loading()) }
        .catch { emit(ApiState.Error(it.message ?: "Cause unknown error")) }

    suspend fun getPagedBoardList(category: BoardCategory, page: Int = 0): List<Board>? {
        val response = api.getPagedBoardListByCategory(category, page)
        if (!response.isSuccessful)
            return null
        return response.body() ?: emptyList()
    }


    companion object {
        private var instance: BoardRepository? = null
        fun getInstance(application: Application): BoardRepository? {
            if (instance == null) instance = BoardRepository(application)
            return instance
        }
    }
}