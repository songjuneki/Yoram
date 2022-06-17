package com.sjk.yoram.Controller

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.zxing.BarcodeFormat
import com.sjk.yoram.databinding.ActivityScannerBinding
import com.sjk.yoram.model.AESUtil
import com.sjk.yoram.model.MyRetrofit
import com.sjk.yoram.model.dto.Attend
import kotlinx.coroutines.*

class ScannerActivity: AppCompatActivity() {
    private val binding by lazy { ActivityScannerBinding.inflate(layoutInflater) }
    private lateinit var scanner: CodeScanner

    private fun checkJob(code: String) = CoroutineScope(Dispatchers.IO).async(start = CoroutineStart.LAZY) {
        val dec = AESUtil().Decrypt(code)
        if (dec == "####") {
            Log.d("JKJK", "QR SCAN :: not id qr!")
            return@async false
        }
        if (dec.substring(0, 14) != "GUSEONGCHURCH;") {
            Log.d("JKJK", "QR SCAN :: not guseong church!")
            return@async false
        }
        if (dec.substring(14, 28) != "BY.SONGJUNEKI;") {
            Log.d("JKJK", "QR SCAN :: wrong signature!")
            return@async false
        }
        val checkerid = intent.getIntExtra("checkerID", 0)
        val wtype = intent.getIntExtra("worshipType", 0)
        val info = dec.substring(28).split(";")

        if (!info[0].startsWith("DATE:")) {
            Log.d("JKJK", "QR SCAN :: qr is something wrong! [DATE]")
            Log.d("JKJK", "info = $info")
            return@async false
        }
        if (!info[1].startsWith("TIME:")) {
            Log.d("JKJK", "QR SCAN :: qr is something wrong! [TIME]")
            return@async false
        }
        if (!info[2].startsWith("ID:")) {
            Log.d("JKJK", "QR SCAN :: qr is something wrong! [ID]")
            return@async false
        }
        val attend = Attend(info[2].substring(3).toInt(), info[0].substring(5), info[1].substring(5), wtype, checkerid)

        return@async MyRetrofit.getMyApi().attendUser(attend)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        scanner = CodeScanner(this, binding.scanner)
        scanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        scanner.formats = listOf(BarcodeFormat.QR_CODE) // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        scanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        scanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        scanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        scanner.isFlashEnabled = false // Whether to enable flash or not

        scanner.decodeCallback = DecodeCallback {
            CoroutineScope(Dispatchers.Main).launch {
                val isSuccess = checkJob(it.text).await()
                if (isSuccess) {
                    binding.scannerVerify.visibility = View.VISIBLE
                    val fadein = ObjectAnimator.ofFloat(binding.scannerVerify, "alpha", 0f, 1f)
                    fadein.addListener(object: Animator.AnimatorListener {
                        override fun onAnimationStart(p0: Animator?) {
                        }

                        override fun onAnimationEnd(p0: Animator?) {
                            val fadeout = ObjectAnimator.ofFloat(binding.scannerVerify, "alpha", 1f, 0f).apply {
                                duration = 1000
                                addListener(object: Animator.AnimatorListener {
                                    override fun onAnimationStart(p0: Animator?) {
                                    }

                                    override fun onAnimationEnd(p0: Animator?) {
                                        binding.scannerVerify.visibility = View.GONE
                                    }

                                    override fun onAnimationCancel(p0: Animator?) {
                                        binding.scannerVerify.visibility = View.GONE
                                    }

                                    override fun onAnimationRepeat(p0: Animator?) {
                                    }

                                })
                                start()
                            }
                            this@ScannerActivity.scanner.startPreview()
                        }

                        override fun onAnimationCancel(p0: Animator?) {
                        }

                        override fun onAnimationRepeat(p0: Animator?) {
                        }

                    })
                    fadein.duration = 1000
                    fadein.start()
                }
            }
        }

        scanner.errorCallback = ErrorCallback {

        }

        binding.scanner.setOnClickListener {
            scanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        scanner.startPreview()
    }

    override fun onPause() {
        scanner.releaseResources()
        super.onPause()
    }
}