package com.sjk.yoram.repository.retrofit

import android.util.Log
import com.sjk.yoram.repository.retrofit.api.BoardApi
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
    const val SERVER = "http://hyuny840501.cafe24.com:8080/api/"

    private fun getRetrofit(): Retrofit {
        val okHttp: OkHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(20, TimeUnit.SECONDS)
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
    val boardApi: BoardApi by lazy { getRetrofit().create(BoardApi::class.java) }


    suspend fun checkServer(): Boolean {
        return try {
            serverApi.serverCheck().isSuccessful
        } catch(e: SocketTimeoutException) {
            false
        } catch (e: ConnectException) {
            false
        }
    }
}