package com.sjk.yoram.view.fragment.main.my.edit

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sjk.yoram.R
import com.sjk.yoram.databinding.DialogAvatarBinding
import com.sjk.yoram.viewmodel.EditViewModel
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import java.io.File

class EditAvatarDialog: BottomSheetDialogFragment() {
    private lateinit var binding: DialogAvatarBinding
    private val viewModel: EditViewModel by activityViewModels()

    private lateinit var file: File
    private lateinit var photoUri: Uri

    private val imageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                openCropper(it)
            }
        }
    }

    private val cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val filePath = file.absolutePath
            openCropper(photoUri)
        }
    }

    private val cropImgResult = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val cropImgUri = result.uriContent
            val input = requireActivity().contentResolver.openInputStream(cropImgUri!!)
            viewModel.avatar.value = BitmapFactory.decodeStream(input)
            dismiss()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogAvatarBinding.inflate(layoutInflater)
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.galleryPickerEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                openGalleryPicker()
            }
        }

        viewModel.cameraPickerEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                openCameraPicker()
            }
        }

        viewModel.deleteAvatarEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                dismiss()
            }
        }
    }


    private fun openGalleryPicker() {
        if (checkReadStoragePermission()) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            imageResult.launch(intent)
        }
    }

    private fun openCameraPicker() {
        if (checkCameraPermission() || checkWriteStoragePermission() || checkReadStoragePermission()) {
            val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            file = File.createTempFile("JPEG_TEMP", ".jpg", storageDir)
            photoUri = FileProvider.getUriForFile(requireContext(), "com.sjk.yoram.fileprovider", file)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            cameraResult.launch(intent)
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


    private fun checkCameraPermission(): Boolean {
        val cameraPermissionCheck = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
        return if (cameraPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA),
                1000
            )
            false
        } else true
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

    private fun checkWriteStoragePermission(): Boolean {
        val readStoragePermissionCheck = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return if (readStoragePermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1200
            )
            false
        } else true
    }



    override fun getTheme(): Int = R.style.DraggableRoundedBottomSheetDialog
}