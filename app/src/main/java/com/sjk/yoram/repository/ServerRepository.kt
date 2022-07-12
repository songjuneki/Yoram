package com.sjk.yoram.repository

import android.app.Application
import com.sjk.yoram.model.MyRetrofit
import com.sjk.yoram.model.dto.Banner

class ServerRepository(private val application: Application) {

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

    companion object {
        private var instance: ServerRepository? = null
        fun getInstance(application: Application): ServerRepository? {
            if (instance == null) instance = ServerRepository(application)
            return instance
        }
    }
}