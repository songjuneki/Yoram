package com.sjk.yoram.view.fragment.main.my.preference.admin

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragMyPrefAdminBannerBinding
import com.sjk.yoram.model.ui.adapter.AdminBannerListAdapter
import com.sjk.yoram.viewmodel.AdminBannerViewModel
import com.sjk.yoram.viewmodel.PrefViewModel

class AdminBannerFragment: Fragment() {
    private lateinit var binding: FragMyPrefAdminBannerBinding
    private lateinit var prefViewModel: PrefViewModel
    private lateinit var viewModel: AdminBannerViewModel

    private val imageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                openCropper(it)
            }
        }
    }

    private val cropImgResult = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
//            val cropImgUri = result.uriContent
            viewModel.uploadNewBanner(result.getBitmap(requireContext()))
        }
    }

    private fun openGalleryPicker() {
        if (checkReadStoragePermission()) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            imageResult.launch(intent)
        }
    }

    private fun openCropper(uri: Uri) {
        cropImgResult.launch(
            options(uri = uri) {
                setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                setCropShape(CropImageView.CropShape.RECTANGLE)
                setActivityMenuIconColor(R.color.md_theme_light_shadow)
                setActivityTitle("사진 수정")
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        prefViewModel = ViewModelProvider(requireActivity())[PrefViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity())[AdminBannerViewModel::class.java]
        binding = FragMyPrefAdminBannerBinding.inflate(layoutInflater)
        binding.prefVM = prefViewModel
        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        viewModel.loadBanners()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.bannerDetailEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(R.id.action_adminBannerFragment_to_editBannerDialogFragment)
            }
        }

        viewModel.editDoneEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                (binding.fragMyPrefAdminBannerBodyRecycler.adapter as AdminBannerListAdapter).notifyDataSetChanged()
            }
        }

        viewModel.applyEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(R.id.action_adminBannerFragment_to_prefApplyDialogFragment)
            }
        }

        viewModel.uploadDoneEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(R.id.action_prefApplyDialogFragment_to_prefFragment)
            }
        }

        viewModel.uploadFailEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                val dialog = AlertDialog.Builder(requireContext())
                dialog.setTitle("오류")
                    .setMessage("배너를 전송하는 중에 오류가 발생했습니다. 다시 시도해 주세요")
                    .setPositiveButton("확인") { d, _ -> d.dismiss()}
                    .show()
            }
        }

        viewModel.uploadNewBannerEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                openGalleryPicker()
            }
        }
    }

    private fun checkReadStoragePermission(): Boolean {
        val readStoragePermissionCheck = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
        return if (readStoragePermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1100
            )
            false
        } else true
    }
}