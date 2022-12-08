package com.sjk.yoram.repository

import android.app.Application
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import com.sjk.yoram.model.MyRetrofit
import com.sjk.yoram.model.dto.Banner
import com.sjk.yoram.model.dto.GiveType
import com.sjk.yoram.model.dto.Juso
import com.sjk.yoram.model.dto.WorshipType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.time.LocalDateTime

class ServerRepository(private val application: Application) {
    suspend fun searchAddress(keyword: String, page: Int = 1): List<Juso> {
        val result = MyRetrofit.serverApi.searchAddress(keyword)
        if (result.isSuccessful)
            return result.body()!!
        return listOf()
    }

    suspend fun getAllBanners(all: Boolean = false): List<Banner> {
        val res = MyRetrofit.serverApi.getBannerList(all)
        if (res.isSuccessful)
            return res.body() ?: listOf()
        return listOf()
    }

    suspend fun deleteBanner(banner: Banner): Boolean {
        val res = MyRetrofit.serverApi.deleteBanner(banner)
        if (res.isSuccessful)
            return res.body() ?: false
        return false
    }

    suspend fun uploadBanners(banners: List<Banner>): Boolean {
        Log.d("JKJK", "upload banners : $banners")
        val res = MyRetrofit.serverApi.editBanners(banners)
        if (res.isSuccessful)
            return !res.body()!!.contains(false)
        return false
    }

    suspend fun uploadNewBanner(img: Bitmap?, owner: Int): Boolean {
        if (img == null) return false

        val storageDir: File? = application.applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("JPEG_UPLOAD_TEMP", ".jpg", storageDir)
        var out: OutputStream? = null

        try {
            out = FileOutputStream(file)
            img.compress(Bitmap.CompressFormat.JPEG, 100, out)
        } catch (e: Exception){
            e.printStackTrace()
            out?.close()
            return false
        }
        out?.close()

        val now = LocalDateTime.now()

        val requestBody = MultipartBody.Part.createFormData("pic", "${now.year}${String.format("%02d", now.monthValue)}${String.format("%02d", now.dayOfMonth)}${String.format("%02d", now.hour)}${String.format("%02d", now.minute)}${String.format("%02d", now.second)}.jpg", file.asRequestBody("image/*".toMediaType()))
        val upload = MyRetrofit.serverApi.uploadNewBanner(requestBody,
            owner.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            "${now.year}-${now.monthValue}-${now.dayOfMonth}".toRequestBody("text/plain".toMediaTypeOrNull()),
            "${now.hour}:${now.minute}:${now.second}".toRequestBody("text/plain".toMediaTypeOrNull())
        )
        return upload.isSuccessful
    }

    suspend fun getMaxWeekOfMonth(year: Int = 0, month: Int = 0): Int {
        val result = MyRetrofit.serverApi.getMaxWeekOfMonth(year, month)
        if (result.isSuccessful)
            return result.body()!!
        return 5
    }

    suspend fun getWorshipList(): List<WorshipType> {
        return MyRetrofit.serverApi.getAllWorship()
    }

    suspend fun getAllGiveTypeList(): List<GiveType> {
        val list = MyRetrofit.serverApi.getAllGiveType()
        if (!list.isSuccessful)
            return emptyList()
        return list.body() ?: emptyList()
    }

    suspend fun getCheckUsingWorshipType(type: Int): Boolean {
        val result = MyRetrofit.serverApi.getCheckWorshipType(type)
        if (!result.isSuccessful)
            return false
        return result.body() ?: false
    }

    suspend fun editWorshipTypeList(wtypeList: List<WorshipType>): Boolean {
        val result = MyRetrofit.serverApi.uploadWorshipTypeList(wtypeList)
        if (!result.isSuccessful)
            return false
        result.body()?.forEach {
            if (it != 1) return false
        }
        return true
    }

    suspend fun getCheckUsingGiveType(type: Int): Boolean {
        val result = MyRetrofit.serverApi.getCheckGiveType(type)
        if (!result.isSuccessful)
            return false
        return result.body() ?: false
    }

    suspend fun editGiveTypeList(giveTypeList: List<GiveType>): Boolean {
        val result = MyRetrofit.serverApi.uploadGiveTypeList(giveTypeList)
        if (!result.isSuccessful)
            return false
        result.body()?.forEach {
            if (it != 1) return false
        }
        return true
    }



    companion object {
        private var instance: ServerRepository? = null
        fun getInstance(application: Application): ServerRepository? {
            if (instance == null) instance = ServerRepository(application)
            return instance
        }
    }
}