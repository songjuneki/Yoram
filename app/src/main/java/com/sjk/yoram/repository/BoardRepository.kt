package com.sjk.yoram.repository

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.paging.*
import com.sjk.yoram.model.BoardPagingSource
import com.sjk.yoram.model.dto.Board
import com.sjk.yoram.model.dto.BoardCategory
import com.sjk.yoram.model.dto.ReservedBoardCategory
import com.sjk.yoram.repository.retrofit.MyRetrofit
import kotlinx.coroutines.flow.*

class BoardRepository(private val application: Application) {
    private val api = MyRetrofit.boardApi
    fun checkInternetConnection(): Boolean {
        try {
            val conManager = application.getSystemService(ConnectivityManager::class.java)
            val currentNetwork = conManager.activeNetwork ?: return false
            val caps = conManager.getNetworkCapabilities(currentNetwork) ?: return false

            return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun getReservedCategoryList(): List<ReservedBoardCategory> {
        val response = api.getReservedBoardCategoryList()
        if (!response.isSuccessful)
            return emptyList()
        return response.body() ?: emptyList()
    }


    fun getBoardPagingDataFlow(category: ReservedBoardCategory?): Flow<PagingData<Board>> {
        val pagingSourceFactory = { BoardPagingSource(this, category?.toBoardCategory()) }

        return Pager(
            config = PagingConfig(pageSize = PAGE_PER_BOARD, enablePlaceholders = false),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }


    suspend fun getBoardList(category: BoardCategory, page: Int = 0): List<Board>? {
        val response = api.getPagedBoardListByCategory(category, page)
        return if (response.isSuccessful)
            response.body() ?: emptyList()
        else
            null
    }


    companion object {
        private const val PAGE_PER_BOARD = 10

        private var instance: BoardRepository? = null
        fun getInstance(application: Application): BoardRepository? {
            if (instance == null) instance = BoardRepository(application)
            return instance
        }
    }
}