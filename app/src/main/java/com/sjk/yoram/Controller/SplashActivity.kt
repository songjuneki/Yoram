package com.sjk.yoram.Controller

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.sjk.yoram.Model.MyRetrofit
import com.sjk.yoram.R
import com.sjk.yoram.databinding.SplashBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SplashActivity: AppCompatActivity() {
    private lateinit var binding: SplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.splash)
        val serverCheck = CoroutineScope(Dispatchers.Default).async { MyRetrofit.checkServer() }

        CoroutineScope(Dispatchers.Main).launch {
            if (serverCheck.await()) {
                Log.d("JKJK", "server success")
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
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