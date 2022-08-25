package com.sjk.yoram.view.fragment.main

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.github.sumimakito.awesomeqr.AwesomeQrRenderer
import com.github.sumimakito.awesomeqr.option.RenderOption
import com.github.sumimakito.awesomeqr.option.color.Color
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.sjk.yoram.view.activity.ScannerActivity
import com.sjk.yoram.viewmodel.MainViewModel
import com.sjk.yoram.model.AESUtil
import com.sjk.yoram.model.FragmentType
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragIdBinding
import com.sjk.yoram.model.dto.MyLoginData
import com.sjk.yoram.model.MyRetrofit
import com.sjk.yoram.model.dto.WorshipType
import com.sjk.yoram.viewmodel.FragIDViewModel
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class IDFragment: Fragment() {
    private lateinit var binding: FragIdBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var viewModel: FragIDViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_id, container, false)
        viewModel = ViewModelProvider(requireActivity())[FragIDViewModel::class.java]
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.idScanner.setOnClickListener {
            val cameraPermissionCheck = ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.CAMERA)
            if (cameraPermissionCheck != PackageManager.PERMISSION_GRANTED) { // 권한이 없는 경우
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.CAMERA),
                    1000
                )
            } else { //권한이 있는 경우
                CoroutineScope(Dispatchers.Main).launch {
                    var selectedWorship = WorshipType(0, "")
                    val worship = MyRetrofit.getMyApi().getAllWorship()
                    val dialog = AlertDialog.Builder(this@IDFragment.requireContext()).apply {
                        setTitle("예배 종류를 선택해주세요")
                        setSingleChoiceItems(worship.map { it.name }.toTypedArray(), -1) { dialog, which ->
                            selectedWorship = worship[which]
                        }
                    }
                    dialog.setPositiveButton("확인") { dialog, which ->
                        val myid = viewModel.user.value!!.id
                        val wtype = selectedWorship.id
                        val intent = Intent(requireActivity(), ScannerActivity::class.java).apply {
                            putExtra("checkerID", myid)
                            putExtra("worshipType", wtype)
                        }
                        startActivity(intent)
                    }
                    dialog.setNegativeButton("취소") { dialog, _ -> dialog.dismiss()}
                    dialog.show()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.countStop()
    }

}