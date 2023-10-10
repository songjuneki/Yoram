package com.sjk.yoram.view.fragment.main.id

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.animation.addListener
import androidx.fragment.app.DialogFragment
import androidx.navigation.navGraphViewModels
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.ScanMode
import com.google.zxing.BarcodeFormat
import com.sjk.yoram.R
import com.sjk.yoram.databinding.DialogScannerBinding
import com.sjk.yoram.viewmodel.FragIDViewModel

class ScannerDialog: DialogFragment() {
    private val viewModel: FragIDViewModel by navGraphViewModels(R.id.navi_id)
    private lateinit var binding: DialogScannerBinding
    private lateinit var scannerView: CodeScannerView
    private lateinit var scanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogScannerBinding.inflate(layoutInflater)
        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
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
            override fun onAnimationStart(animation: Animator) {
                binding.dialogScannerSuccess.alpha = 1f
            }

            override fun onAnimationEnd(animation: Animator) {
                ObjectAnimator.ofFloat(binding.dialogScannerSuccess, "alpha", 1f, 0f).apply {
                    duration = 1000
                    start()
                }.addListener(onEnd = { viewModel.notifyEnded() })
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        binding.dialogScannerFailure.addAnimatorListener(object: Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                binding.dialogScannerFailure.alpha = 1f
            }

            override fun onAnimationEnd(animation: Animator) {
                ObjectAnimator.ofFloat(binding.dialogScannerFailure, "alpha", 1f, 0f).apply {
                    duration = 1000
                    start()
                }.addListener(onEnd = { viewModel.notifyEnded() })
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })


    }

}