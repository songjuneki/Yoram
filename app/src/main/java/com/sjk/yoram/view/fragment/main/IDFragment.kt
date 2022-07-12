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
import com.sjk.yoram.model.MyLoginData
import com.sjk.yoram.model.MyRetrofit
import com.sjk.yoram.model.dto.WorshipType
import com.sjk.yoram.viewmodel.FragIDViewModel
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class IDFragment: Fragment() {
    //private val binding by lazy { FragIdBinding.inflate(layoutInflater) }
    private lateinit var binding: FragIdBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: FragIDViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val title = requireArguments().getString("title")
        //Log.d("jk", "${title} 오픈")
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_id, container, false)
//        viewModel = ViewModelProvider(requireActivity()).get(FragIDViewModel::class.java)
        binding.vm = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.loginState.observe(viewLifecycleOwner) {
            var user = mainViewModel.loginData.value ?: MyLoginData()
            viewModel.setUser(user)
            if (user.permission > 3) {
                binding.idScanner.visibility = View.VISIBLE
            } else {
                binding.idScanner.visibility = View.GONE
            }
        }

        mainViewModel.currentFragmentType.observe(viewLifecycleOwner) {
            if (it == FragmentType.Fragment_ID) {
                makeQR(viewModel.user.value!!)
                viewModel.countstop()
                viewModel.countJob.start()
            } else {
                viewModel.countstop()
            }
        }

        viewModel.user.observe(viewLifecycleOwner) {
            binding.idAvatarImgv.load("http://3.39.51.49:8080/api/user/avatar?id=${it.id}") {
                crossfade(true)
                transformations(CircleCropTransformation())
            }
            binding.userData = it
            if (it.department == -1) {
                binding.idNotice.visibility = View.VISIBLE
                binding.idCode.visibility = View.INVISIBLE
                binding.idTimer.visibility = View.INVISIBLE
            } else {
                binding.idNotice.visibility = View.INVISIBLE
                binding.idCode.visibility = View.VISIBLE
                binding.idTimer.visibility = View.VISIBLE
            }
        }

        viewModel.timer.observe(viewLifecycleOwner) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.timer = it
                if (it == 15) {
                    try {
                        makeQR(viewModel.user.value!!)
                    } catch (e: Exception) {
                        viewModel.countJob.cancel()
                    }
                }
            }
        }

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
                        val myid = mainViewModel.loginData.value!!.id
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


    companion object {
        fun newInstance(title:String) = IDFragment().apply {
            arguments = Bundle().apply {
                putString("title", title)
            }
        }
    }

    private fun makeQROption(user: MyLoginData, special: Boolean = false) = RenderOption().apply {
        val date = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val timeFormat = SimpleDateFormat("HHmmss")
        val p = "GUSEONGCHURCH;BY.SONGJUNEKI;DATE:${dateFormat.format(date)};TIME:${timeFormat.format(date)};ID:${user.id}"
        content = AESUtil().Encrypt(p)
        size = 1000
        borderWidth = 10
        ecl = ErrorCorrectionLevel.M
        patternScale = 0.7f
        roundedPatterns = false
        clearBorder = true

        val setColor = Color()
        setColor.background = android.graphics.Color.TRANSPARENT
        setColor.dark = android.graphics.Color.BLACK
        setColor.light = android.graphics.Color.WHITE
        setColor.auto = false
        color = setColor
    }

    private fun makeQR(user:MyLoginData) {
        val option = makeQROption(user)
        try {
            val result = AwesomeQrRenderer.render(option)
            if (result.bitmap != null) {
                binding.idCode.load(result.bitmap)
            } else Toast.makeText(this@IDFragment.context, "QR Gen Fail", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            throw e
        }

    }
}