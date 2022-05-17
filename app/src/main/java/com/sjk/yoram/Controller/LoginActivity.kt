package com.sjk.yoram.Controller

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sjk.yoram.MainVM
import com.sjk.yoram.Model.LoginState
import com.sjk.yoram.Model.MyRetrofit
import com.sjk.yoram.Model.dto.User
import com.sjk.yoram.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.security.MessageDigest
import kotlin.experimental.and

class LoginActivity: AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    private var loginState = LoginState.NONE

    private var id = -1
    private var name: String = ""
    private var bd: String = ""
    private var pw: String = ""
    private lateinit var user: User

    private suspend fun nameCheckJob() = CoroutineScope(Dispatchers.Main).async {
        this@LoginActivity.name = binding.loginNameInput.text.toString()
        val findUsers = MyRetrofit.getMyApi().getUserByName(this@LoginActivity.name)
        if (findUsers.size == 1)
            user = findUsers.first()
        Log.d("JKJK", "finduser = ${findUsers}")
        findUsers.size
    }
    private suspend fun bdCheckJob() = CoroutineScope(Dispatchers.Main).async {
        this@LoginActivity.bd = binding.loginBdInput.text.toString()
        val findUsers = MyRetrofit.getMyApi().getUserByNameAndBD(this@LoginActivity.name, this@LoginActivity.bd)
        if (findUsers.size == 1)
            user = findUsers.first()
        findUsers.size
    }
    private suspend fun userCheckJob() = CoroutineScope(Dispatchers.Main).async {
        this@LoginActivity.pw = binding.loginPwInput.text.toString()
        val key = EncryptKey(pw)
        user.pw == key
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.loginOkButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                when (loginState) {
                    LoginState.NONE, LoginState.NAME_FAIL -> {
                        val find = nameCheckJob().await()
                        if (find == 0) {
                            binding.loginNameInput.error = "존재하지 않는 이름입니다."
                            this@LoginActivity.loginState = LoginState.NAME_FAIL
                        } else if (find == 1) {
                            binding.loginPwInputLayout.visibility = View.VISIBLE
                            this@LoginActivity.loginState = LoginState.NAME_SUCCESS
                        } else {
                            binding.loginBdInputLayout.visibility = View.VISIBLE
                            this@LoginActivity.loginState = LoginState.NAME_SUCCESS_NEED_BD
                        }
                    }
                    LoginState.BD_FAIL, LoginState.NAME_SUCCESS_NEED_BD -> {
                        val find = bdCheckJob().await()
                        if (find != 1) {
                            binding.loginBdInput.error = "잘못 입력하였습니다."
                            loginState = LoginState.BD_FAIL
                        } else {
                            binding.loginBdInput.error = ""
                            binding.loginPwInputLayout.visibility = View.VISIBLE
                            loginState = LoginState.BD_SUCCESS_NEED_PW
                        }
                    }
                    LoginState.PW_FAIL, LoginState.NAME_SUCCESS, LoginState.BD_SUCCESS_NEED_PW -> {
                        val check = userCheckJob().await()
                        if (check) {
                            loginState = LoginState.LOGIN
                            val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                                putExtra("LOGIN_ID", user.id)
                                putExtra("LOGIN_PW", user.pw)
                            }
                            this@LoginActivity.setResult(RESULT_OK, intent)
                            this@LoginActivity.finish()
                        } else {
                            loginState = LoginState.PW_FAIL
                        }
                    }
                }
            }
        }

        binding.loginCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }


    }
    private fun EncryptKey(key: String): String {
        val encoder = MessageDigest.getInstance("SHA-256")
        val byteArray = encoder.digest(key.toByteArray())

        val enc = StringBuffer()
        for (byte in byteArray) {
            val hashedByte = (byte.and((0xff).toByte()) + 0x100).toString(16)
            if (hashedByte.length > 2)
                enc.append(hashedByte.substring(1))
            else
                enc.append(hashedByte)
        }
        return enc.toString()
    }
}