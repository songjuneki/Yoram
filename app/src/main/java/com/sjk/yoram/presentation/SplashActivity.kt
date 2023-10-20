package com.sjk.yoram.presentation

import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import com.sjk.yoram.presentation.common.model.LoginCheck
import com.sjk.yoram.data.repository.retrofit.MyRetrofit
import com.sjk.yoram.R
import com.sjk.yoram.data.entity.LoginResult
import com.sjk.yoram.databinding.SplashBinding
import com.sjk.yoram.presentation.init.InitActivity
import com.sjk.yoram.presentation.main.MainActivity
import kotlinx.coroutines.*

class SplashActivity: AppCompatActivity() {
    private lateinit var binding: SplashBinding
    private lateinit var splashScreen: SplashScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashScreen = installSplashScreen()
        binding = DataBindingUtil.setContentView(this, R.layout.splash)


        CoroutineScope(Dispatchers.Main).launch {
            if (!checkInternetConnection()) {
                AlertDialog.Builder(this@SplashActivity)
                    .setMessage("인터넷 연결이 없습니다. 인터넷 연결을 확인해 주세요")
                    .setPositiveButton("확인") { _, _ -> ActivityCompat.finishAffinity(this@SplashActivity) }
                    .create()
                    .show()
                delay(50000)
            }

            val serverCheck = CoroutineScope(Dispatchers.Default).async { MyRetrofit.checkServer() }
            suspend fun isLogin(id:Int, pw:String) = CoroutineScope(Dispatchers.Default).async { MyRetrofit.userApi.check(
                LoginCheck(id, pw)
            ) }

            if (serverCheck.await()) {
                val initIntent = Intent(this@SplashActivity, InitActivity::class.java)
                val homeIntent = Intent(this@SplashActivity, MainActivity::class.java)
                val sharedPref = this@SplashActivity.getSharedPreferences(getString(R.string.YORAM_LOCAL_PREF), MODE_PRIVATE)
                val isInit = sharedPref.getBoolean(getString(R.string.YORAM_LOCAL_PREF_ISINIT), true)

                if (isInit) {
                    // 앱 초기 상태일시
                    initIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    initIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(initIntent)
                    finish()
                } else {
                    // 앱 초기 진행 했을 시
                    val localId = sharedPref.getInt(getString(R.string.YORAM_LOCAL_PREF_MYID), -1)
                    val localPw = sharedPref.getString(getString(R.string.YORAM_LOCAL_PREF_MYPW), "@")!!
                    val builder = AlertDialog.Builder(this@SplashActivity)
                    val idCheck = isLogin(localId, localPw).await().body()

                    if ((idCheck != LoginResult.LOGIN && localId != -1) || isInit) {
                        builder.setMessage("로그인한 정보가 다릅니다. 다시 로그인 해주세요")
                            .setPositiveButton("확인", DialogInterface.OnClickListener{_, _ -> startActivity(initIntent); return@OnClickListener})    // 로그인 화면 인텐트로 수정
                            .create().show()
                    } else {
                        startActivity(homeIntent)       // 메인화면으로
                        finish()
                    }
                }
            } else {
                val builder = AlertDialog.Builder(this@SplashActivity)
                builder.setMessage("서버 연결에 실패하였습니다.\n 애플리케이션을 다시 실행해 주세요.\n 문제가 계속 되면 관리자에게 문의바랍니다.")
                    .setPositiveButton("확인"
                    ) { _, _ -> ActivityCompat.finishAffinity(this@SplashActivity) }
                    .create().show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    override fun onStop() {
        super.onStop()
        overridePendingTransition(0, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        overridePendingTransition(0 , 0)
    }


    private suspend fun checkInternetConnection(): Boolean = withContext(Dispatchers.Main) {
        try {
            val conManager = application.getSystemService(ConnectivityManager::class.java)
            val currentNetwork = conManager.activeNetwork ?: return@withContext false
            val caps = conManager.getNetworkCapabilities(currentNetwork) ?: return@withContext false

            return@withContext caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_USB)
        } catch (e: Exception) {
            return@withContext false
        }
    }

}