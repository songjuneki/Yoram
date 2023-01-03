package com.sjk.yoram.model

import android.util.Log
import com.sjk.yoram.repository.retrofit.api.DepartmentApi
import com.sjk.yoram.repository.retrofit.api.ServerApi
import com.sjk.yoram.repository.retrofit.api.UserApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

object MyRetrofit {
    const val SERVER = "http://3.39.51.49:8080/api/"

    private fun getRetrofit(): Retrofit {
        val okHttp: OkHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(SERVER)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val serverApi: ServerApi by lazy { getRetrofit().create(ServerApi::class.java) }
    val userApi: UserApi by lazy { getRetrofit().create(UserApi::class.java) }
    val dptmentApi: DepartmentApi by lazy { getRetrofit().create(DepartmentApi::class.java) }


    suspend fun checkServer(): Boolean {
        try {
            serverApi.serverCheck()
        }catch(e: SocketTimeoutException){
            Log.d("JKJk", "retrofit exception : $e")
            return false
        } catch (e: ConnectException) {
            Log.d("JKJK", "retrofit exception : $e")
            return false
        }
        return true
    }
}