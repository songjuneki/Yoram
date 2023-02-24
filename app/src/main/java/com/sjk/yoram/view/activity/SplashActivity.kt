package com.sjk.yoram.view.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.sjk.yoram.model.LoginCheck
import com.sjk.yoram.repository.retrofit.MyRetrofit
import com.sjk.yoram.R
import com.sjk.yoram.databinding.SplashBinding
import com.sjk.yoram.view.activity.main.MainActivity
import kotlinx.coroutines.*
import kotlin.system.exitProcess

class SplashActivity: AppCompatActivity() {
    private lateinit var binding: SplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.splash)
        val serverCheck = CoroutineScope(Dispatchers.Default).async { MyRetrofit.checkServer() }
        suspend fun isLogin(id:Int, pw:String) = CoroutineScope(Dispatchers.Default).async { MyRetrofit.userApi.check(LoginCheck(id, pw)) }

        CoroutineScope(Dispatchers.Main).launch {
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
                    val idCheck = isLogin(localId, localPw).await()

                    if ((!idCheck && localId != -1) || isInit) {
                        builder.setMessage("로그인한 정보가 다릅니다. 다시 로그인 해주세요")
                            .setPositiveButton("확인", DialogInterface.OnClickListener{_, _ -> startActivity(initIntent); return@OnClickListener})    // 로그인 화면 인텐트로 수정
                            .create().show()
                    } else {
                        startActivity(homeIntent)       // 메인화면으로
                        finish()
                    }
                }
            }
            else {
                val builder = AlertDialog.Builder(this@SplashActivity)
                builder.setMessage("서버 연결에 실패하였습니다.\n 애플리케이션을 다시 실행해 주세요.\n 문제가 계속 되면 관리자에게 문의바랍니다.")
                    .setPositiveButton("확인"
                    ) { _, _ -> ActivityCompat.finishAffinity(this@SplashActivity); exitProcess(0) }
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

}