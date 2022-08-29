package com.sjk.yoram.repository

import android.app.Application
import android.util.Log
import com.sjk.yoram.model.MyRetrofit
import com.sjk.yoram.model.dto.Banner
import com.sjk.yoram.model.dto.Juso
import com.sjk.yoram.model.dto.WorshipType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.internal.wait

class ServerRepository(private val application: Application) {
    suspend fun searchAddress(keyword: String, page: Int = 1): List<Juso> {
        val result = MyRetrofit.serverApi.searchAddress(keyword)
        if (result.isSuccessful)
            return result.body()!!
        return listOf()
    }

    suspend fun getAllBanner(): List<Banner> {
        val idList = MyRetrofit.serverApi.getBannerList()
        val bannerList: MutableList<Banner> = mutableListOf()
        if (idList.isSuccessful) {
            idList.body()!!.forEach {
                bannerList.add(MyRetrofit.serverApi.getBannerInfo(it, true))
            }
        }
        return bannerList
    }

    suspend fun getMaxWeekOfMonth(year: Int = 0, month: Int = 0): Int {
        val result = MyRetrofit.serverApi.getMaxWeekOfMonth(year, month)
        if (result.isSuccessful)
            return result.body()!!
        return 5
    }

    suspend fun getWorshipList(): List<WorshipType> {
        val list = MyRetrofit.serverApi.getAllWorship()
        Log.d("JKJK", "worship list = $list")
        return list
    }

    companion object {
        private var instance: ServerRepository? = null
        fun getInstance(application: Application): ServerRepository? {
            if (instance == null) instance = ServerRepository(application)
            return instance
        }
    }
}