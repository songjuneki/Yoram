package com.sjk.yoram.Controller

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.sjk.yoram.Model.LoginCheck
import com.sjk.yoram.Model.MyRetrofit
import com.sjk.yoram.R
import com.sjk.yoram.databinding.SplashBinding
import kotlinx.coroutines.*

class SplashActivity: AppCompatActivity() {
    private lateinit var binding: SplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.splash)
        val serverCheck = CoroutineScope(Dispatchers.Default).async { MyRetrofit.checkServer() }
        suspend fun isLogin(id:Int, pw:String) = CoroutineScope(Dispatchers.Default).async { MyRetrofit.getMyApi().checkMyUser(LoginCheck(id, pw)) }

        CoroutineScope(Dispatchers.Main).launch {
            if (serverCheck.await()) {
                Log.d("JKJK", "server success")
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                val sharedPref = this@SplashActivity.getSharedPreferences(getString(R.string.YORAM_LOCAL_PREF), MODE_PRIVATE)
                val localId = sharedPref.getInt(getString(R.string.YORAM_LOCAL_PREF_MYID), -1)
                val localPw = sharedPref.getString(getString(R.string.YORAM_LOCAL_PREF_MYPW), "@")!!

                val builder = AlertDialog.Builder(this@SplashActivity)

                Log.d("JKJK", "SplashActivity -- id=${localId}, pw=${localPw}")
                val idCheck = isLogin(localId, localPw).await()

                if (!idCheck && localId != -1) {
                    builder.setMessage("로그인한 정보가 다릅니다. 다시 로그인 해주세요")
                        .setPositiveButton("확인", DialogInterface.OnClickListener{_, _ -> startActivity(intent); return@OnClickListener})
                        .create().show()
                } else {
                    intent.putExtra("loginedID", localId)
                    intent.putExtra("isLogin", idCheck)
                    startActivity(intent)
                }
            }
            else {
                Log.d("JKJK", "server failure")
                val builder = AlertDialog.Builder(this@SplashActivity)
                builder.setMessage("서버 연결에 실패하였습니다.\n 애플리케이션을 다시 실행해 주세요.\n 문제가 계속 되면 관리자에게 문의바랍니다.")
                    .setPositiveButton("확인", DialogInterface.OnClickListener { _, _ -> ActivityCompat.finishAffinity(this@SplashActivity); System.exit(0)})
                    .create().show()
            }
        }
    }

}