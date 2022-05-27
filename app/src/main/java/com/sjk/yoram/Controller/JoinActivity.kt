package com.sjk.yoram.Controller

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Base64InputStream
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sjk.yoram.Model.NewUser
import com.sjk.yoram.Model.dto.User
import com.sjk.yoram.databinding.ActivityJoinUserBinding
import com.sjk.yoram.viewmodel.JoinUserViewModel
import okhttp3.internal.and
import java.security.DigestException
import java.security.MessageDigest
import java.util.*
import kotlin.experimental.and

class JoinActivity: AppCompatActivity() {
    private val binding by lazy { ActivityJoinUserBinding.inflate(layoutInflater) }
    private val viewmodel: JoinUserViewModel by viewModels()
    lateinit var reqBool: MutableList<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.vm = viewmodel

        var newUser = NewUser()
        reqBool = mutableListOf(false, false, false, false)

        binding.joinFnameEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                reqBool[0] = !p0.isNullOrEmpty()
                buttonEnabler()
            }
        })

        binding.joinLnameEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                reqBool[1] = !p0.isNullOrEmpty()
                buttonEnabler()
            }
        })

        binding.joinPwEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                reqBool[2] = !p0.isNullOrEmpty()
                buttonEnabler()
            }
        })

        binding.joinPwCheckEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                val inputed = binding.joinPwEt.text
                if (inputed.contentEquals(p0)) {
                    reqBool[3] = true
                } else {
                    reqBool[3] = false
                    binding.joinPwCheckEtLayout.error = "비밀번호를 확인해 주세요"
                }
                buttonEnabler()
            }
        })


        binding.joinDoneButton.setOnClickListener {
//            newUser.fname = binding.joinFnameEt.text.toString()
//            newUser.lname = binding.joinLnameEt.text.toString()
//            newUser.sex = binding.joinSexM.isChecked
//            val key = binding.joinPwEt.text.toString()
//            newUser.pw = EncryptKey(key)
//
//            newUser.tel1 = binding.joinTel1Et.text.toString()
//            newUser.tel2 = binding.joinTel2Et.text.toString()
//            newUser.address = binding.joinAddressEt.text.toString()
//            newUser.carno = binding.joinCarnoEt.text.toString()

            newUser.fname = viewmodel.fname.value!!
            newUser.lname = viewmodel.lname.value!!
            newUser.sex = viewmodel.sex.value!!
            val key = viewmodel.pw.value!!
            newUser.pw = EncryptKey(key)

            newUser.tel1 = binding.joinTel1Et.text.toString()
            newUser.tel2 = binding.joinTel2Et.text.toString()
            newUser.address = binding.joinAddressEt.text.toString()
            newUser.carno = binding.joinCarnoEt.text.toString()

            Toast.makeText(this, "newUser = ${newUser}", Toast.LENGTH_SHORT).show()
        }

        binding.joinCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun buttonEnabler() {
        this.reqBool.forEach {
            if (!it) {
                binding.joinDoneButton.isClickable = binding.joinAgreeCb.isChecked
                return
            }
            binding.joinDoneButton.isClickable = binding.joinAgreeCb.isChecked
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