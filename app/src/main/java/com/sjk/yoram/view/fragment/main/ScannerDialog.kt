package com.sjk.yoram.view.fragment.main

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.ScanMode
import com.google.zxing.BarcodeFormat
import com.sjk.yoram.R
import com.sjk.yoram.databinding.DialogScannerBinding
import com.sjk.yoram.viewmodel.FragIDViewModel

class ScannerDialog: DialogFragment() {
    private lateinit var viewModel: FragIDViewModel
    private lateinit var binding: DialogScannerBinding
    private lateinit var scannerView: CodeScannerView
    private lateinit var scanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity())[FragIDViewModel::class.java]
        binding = DialogScannerBinding.inflate(layoutInflater)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.showToast.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.frontCam.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                scanner.camera = if (it) CodeScanner.CAMERA_FRONT else CodeScanner.CAMERA_BACK
            }
        }

        viewModel.scanEnabled.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                if (it)
                    scanner.scanMode = ScanMode.CONTINUOUS
                else
                    scanner.scanMode = ScanMode.PREVIEW
                scanner.startPreview()
            }
        }

        viewModel.scanResult.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) binding.dialogScannerSuccess.playAnimation() else binding.dialogScannerFailure.playAnimation()
            }
        }

    }

    override fun getTheme(): Int = R.style.FullScreenDialog

    override fun onResume() {
        super.onResume()
        scanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        scanner.releaseResources()
    }

    private fun initView() {
        scannerView = binding.dialogScannerScanner
        scannerView.setOnClickListener { scanner.startPreview() }
        scanner = CodeScanner(requireContext(), scannerView).apply {
            camera = CodeScanner.CAMERA_BACK
            formats = listOf(BarcodeFormat.QR_CODE)
            autoFocusMode = AutoFocusMode.CONTINUOUS
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false
            decodeCallback = viewModel.decoder
        }

        binding.dialogScannerSuccess.addAnimatorListener(object: Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
                binding.dialogScannerSuccess.alpha = 1f
            }
            override fun onAnimationEnd(p0: Animator?) {
                ObjectAnimator.ofFloat(binding.dialogScannerSuccess, "alpha", 1f, 0f).apply {
                    duration = 1000
                    start()
                }.addListener(object: Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator?) {
                    }
                    override fun onAnimationEnd(p0: Animator?) {
                        viewModel.notifyEnded()
                    }
                    override fun onAnimationCancel(p0: Animator?) {
                    }
                    override fun onAnimationRepeat(p0: Animator?) {
                    }
                })
            }
            override fun onAnimationCancel(p0: Animator?) {
            }
            override fun onAnimationRepeat(p0: Animator?) {
            }
        })
        binding.dialogScannerFailure.addAnimatorListener(object: Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
                binding.dialogScannerFailure.alpha = 1f
            }
            override fun onAnimationEnd(p0: Animator?) {
                ObjectAnimator.ofFloat(binding.dialogScannerFailure, "alpha", 1f, 0f).apply {
                    duration = 1000
                    start()
                }.addListener(object: Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator?) {
                    }
                    override fun onAnimationEnd(p0: Animator?) {
                        viewModel.notifyEnded()
                    }
                    override fun onAnimationCancel(p0: Animator?) {
                    }
                    override fun onAnimationRepeat(p0: Animator?) {
                    }
                })
            }
            override fun onAnimationCancel(p0: Animator?) {
            }
            override fun onAnimationRepeat(p0: Animator?) {
            }
        })


    }

}