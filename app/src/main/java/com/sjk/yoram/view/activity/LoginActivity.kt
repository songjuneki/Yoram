package com.sjk.yoram.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.sjk.yoram.model.LoginState
import com.sjk.yoram.model.MyRetrofit
import com.sjk.yoram.model.dto.User
import com.sjk.yoram.databinding.ActivityLoginBinding
import com.sjk.yoram.view.activity.main.MainActivity
import kotlinx.coroutines.*
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
        val findUsers = MyRetrofit.userApi.get(this@LoginActivity.name)
        if (findUsers.size == 1)
            user = findUsers.first()
        Log.d("JKJK", "finduser = ${findUsers}")
        findUsers.size
    }
    private suspend fun bdCheckJob() = CoroutineScope(Dispatchers.Main).async {
        this@LoginActivity.bd = binding.loginBdInput.text.toString()
        val findUsers = MyRetrofit.userApi.get(this@LoginActivity.name, this@LoginActivity.bd)
        if (findUsers.size == 1)
            user = findUsers.first()
        findUsers.size
    }
    private suspend fun userCheckJob() = CoroutineScope(Dispatchers.Main).async {
        this@LoginActivity.pw = binding.loginPwInput.text.toString()
        val key = EncryptKey(pw)
        user.pw == key
    }

    private suspend fun doneBtnJob() =
        CoroutineScope(Dispatchers.Main).async {
            when (loginState) {
                LoginState.NONE, LoginState.NAME_FAIL -> {
                    val find = nameCheckJob().await()
                    if (find == 0) {
                        binding.loginNameInput.error = "존재하지 않는 이름입니다."
                        this@LoginActivity.loginState = LoginState.NAME_FAIL
                        return@async true
                    } else if (find == 1) {
                        binding.loginPwInputLayout.visibility = View.VISIBLE
                        this@LoginActivity.loginState = LoginState.NAME_SUCCESS
                        binding.loginPwInput.requestFocus()
                        return@async true
                    } else {
                        binding.loginBdInputLayout.visibility = View.VISIBLE
                        this@LoginActivity.loginState = LoginState.NAME_SUCCESS_NEED_BD
                        binding.loginBdInput.requestFocus()
                        return@async true
                    }
                }
                LoginState.BD_FAIL, LoginState.NAME_SUCCESS_NEED_BD -> {
                    val find = bdCheckJob().await()
                    if (find != 1) {
                        binding.loginBdInput.error = "잘못 입력하였습니다."
                        loginState = LoginState.BD_FAIL
                        return@async true
                    } else {
                        binding.loginBdInput.error = ""
                        binding.loginPwInputLayout.visibility = View.VISIBLE
                        loginState = LoginState.BD_SUCCESS_NEED_PW
                        return@async true
                    }
                    return@async false
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
                        return@async false
                    } else {
                        loginState = LoginState.PW_FAIL
                        binding.loginPwInput.error = "잘못 입력하였습니다."
                        return@async true
                    }
                }
                else -> { }
            }
            return@async true
        }


    private val joinResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {  // 계정 생성 성공
            val data = it.data!!
            val joinIn = data.getIntExtra("NEW_USER_ID", -1)
            val joinPw = data.getStringExtra("NEW_USER_PW")

            val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                putExtra("LOGIN_ID", joinIn)
                putExtra("LOGIN_PW", joinPw)
            }
            this.setResult(RESULT_OK, intent)
            this.finish()
        } else if (it.resultCode == Activity.RESULT_CANCELED) {
            // 계정 생성 닫음
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.loginNameInput.setOnEditorActionListener { textView, i, keyEvent ->
            var flag = true
            CoroutineScope(Dispatchers.Main).async {
                flag = doneBtnJob().await()
            }.start()
            return@setOnEditorActionListener flag
        }
        binding.loginBdInput.setOnEditorActionListener { textView, i, keyEvent ->
            var flag = true
            CoroutineScope(Dispatchers.Main).async {
                flag = doneBtnJob().await()
            }.start()
            return@setOnEditorActionListener flag
        }
        binding.loginPwInput.setOnEditorActionListener { textView, i, keyEvent ->
            var flag = true
            CoroutineScope(Dispatchers.Main).async {
                flag = doneBtnJob().await()
            }.start()
            return@setOnEditorActionListener flag
        }

        binding.loginOkButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                doneBtnJob().await()
            }
        }

        binding.loginCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        binding.loginJoinBtn.setOnClickListener {
            val joinIntent = Intent(this, JoinActivity::class.java)
            joinResult.launch(joinIntent)
        }

        binding.loginFindButton.setOnClickListener {

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