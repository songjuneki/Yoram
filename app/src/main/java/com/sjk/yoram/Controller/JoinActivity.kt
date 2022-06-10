package com.sjk.yoram.Controller

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.sjk.yoram.model.MyRetrofit
import com.sjk.yoram.model.NewUser
import com.sjk.yoram.databinding.ActivityJoinUserBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest
import kotlin.experimental.and

class JoinActivity: AppCompatActivity() {
    private val binding by lazy { ActivityJoinUserBinding.inflate(layoutInflater) }
    private lateinit var reqBool: MutableList<Boolean>
    private lateinit var policyIntent: Intent

    private val bdBool: MutableList<Boolean> = mutableListOf(false, false, false)
    private var by = ""
    private var bm = ""
    private var bd = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        var newUser = NewUser()
        reqBool = mutableListOf(false, false, false, false, true)

        policyIntent = Intent(this, PolicyActivity::class.java)

        binding.joinFnameEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                reqBool[0] = !p0.isNullOrEmpty()
                Log.d("JKJK", "reqBool -- $reqBool")
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
                Log.d("JKJK", "reqBool -- $reqBool")
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
                Log.d("JKJK", "reqBool -- $reqBool")
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
                if (p0!!.contentEquals(inputed)) {
                    reqBool[3] = true
                    binding.joinPwCheckEtLayout.error = ""
                } else {
                    reqBool[3] = false
                    binding.joinPwCheckEtLayout.error = "비밀번호를 확인해 주세요"
                }
                Log.d("JKJK", "reqBool -- $reqBool")
                buttonEnabler()
            }
        })

        binding.joinAgreeCb.setOnCheckedChangeListener { _, _ ->
            buttonEnabler()
            Log.d("JKJK", "bdBool -- $bdBool")
        }

        binding.joinDoneBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                newUser.fname = binding.joinFnameEt.text.toString()
                newUser.lname = binding.joinLnameEt.text.toString()
                newUser.sex = binding.joinSexM.isChecked
                val key = binding.joinPwEt.text.toString()
                newUser.pw = EncryptKey(key)

                if (newUser.birth.length < 8)
                    newUser.birth = ""

                val result = MyRetrofit.getMyApi().insertNewUser(newUser)
                Log.d("JKJK", "result = ${result}")

                this@JoinActivity.finish()
            }
        }

        binding.joinByEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (!p0.isNullOrEmpty() && p0.length == 4) {
                    bdBool[0] = true
                    bdStateChanger()
                    by = p0.toString()
                    newUser.birth = "$by$bm$bd"
                } else if (!p0.isNullOrEmpty() && p0.length < 4) {
                    bdBool[0] = false
                    bdStateChanger()
                    newUser.birth = ""
                } else {
                    bdBool[0] = false
                    bdStateChanger()
                    newUser.birth = ""
                }
                Log.d("JKJK", "bdBool -- $bdBool")
            }
        })

        binding.joinBdSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                bdBool[1] = true
                bdStateChanger()
                bm = "%02d".format(p2+1)
                newUser.birth = "$by$bm$bd"
                Log.d("JKJK", "bdBool -- $bdBool")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                bdBool[1] = false
                bdStateChanger()
                newUser.birth = ""
                Log.d("JKJK", "bdBool -- $bdBool")
            }
        }

        binding.joinBdEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (!p0.isNullOrEmpty()) {
                    bdBool[2] = true
                    bdStateChanger()
                    bd = "%02d".format(p0.toString().toInt())
                    newUser.birth = "$by$bm$bd"
                } else {
                    bdBool[2] = false
                    bdStateChanger()
                    newUser.birth = ""
                }
                Log.d("JKJK", "bdBool -- $bdBool")
            }
        })


        binding.joinTel1Et.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        binding.joinTel1Et.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                newUser.tel1 = p0!!.toString()
            }
        })

        binding.joinPolicyTv.setOnClickListener {
            if (policyIntent != null)
                startActivity(policyIntent)
        }

        binding.joinCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
        buttonEnabler()
        binding.joinBdSpinner.prompt = "월"
    }


    private fun buttonEnabler() {
        this.reqBool.forEach {
            if (!it) {
                binding.joinDoneBtn.isClickable = false
                binding.joinDoneBtn.isEnabled = false
                return@forEach
            }
            binding.joinDoneBtn.isClickable = binding.joinAgreeCb.isChecked
            binding.joinDoneBtn.isEnabled = binding.joinAgreeCb.isChecked
        }
    }

    private fun bdStateChanger() {
        val s = bdBool[0].xor(bdBool[1])
        this.reqBool[4] = !s.xor(!bdBool[2])
        buttonEnabler()
        Log.d("JKJK", "bdBool -- $bdBool")
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