package com.sjk.yoram.view.fragment.main.id

import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sjk.yoram.R
import com.sjk.yoram.databinding.DialogScannerInitialBinding
import com.sjk.yoram.viewmodel.FragIDViewModel

class ScannerInitDialog: BottomSheetDialogFragment() {
    private val viewModel: FragIDViewModel by navGraphViewModels(R.id.navi_id)
    private lateinit var binding: DialogScannerInitialBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogScannerInitialBinding.inflate(layoutInflater)
        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.openScannerEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                if (checkCameraPermission()) {
                    findNavController().navigate(it)
                }
            }
        }

    }

    private fun checkCameraPermission(): Boolean {
        val cameraPermissionCheck = ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.CAMERA)
        return if (cameraPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA),
                1000
            )
            false
        } else true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun getTheme(): Int = R.style.DraggableRoundedBottomSheetDialog

}