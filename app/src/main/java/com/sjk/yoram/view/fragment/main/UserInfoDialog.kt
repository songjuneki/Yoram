package com.sjk.yoram.view.fragment.main

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sjk.yoram.R
import com.sjk.yoram.databinding.DialogUserInfoBinding
import com.sjk.yoram.viewmodel.FragDptmentViewModel

class UserInfoDialog: BottomSheetDialogFragment() {
    private lateinit var dptViewModel: FragDptmentViewModel
    private lateinit var binding: DialogUserInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dptViewModel = ViewModelProvider(requireActivity())[FragDptmentViewModel::class.java]
        binding = DialogUserInfoBinding.inflate(layoutInflater)
        binding.vm = dptViewModel
        initView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dptViewModel.userCallEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                Log.d("JKJK", "call to : ${it.replace("-", "")}")
                val dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${it.replace("-", "")}"))
                startActivity(dial)
            }
        }

        dptViewModel.userMsgEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                val msg = Intent(Intent.ACTION_SENDTO, Uri.parse("sms:${it.replace("-", "")}"))
                msg.putExtra("sms_body", "")
                startActivity(msg)
            }
        }
    }

    private fun initView() {
        binding.dialogUserInfoClose.setOnClickListener { dismiss() }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            (this as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
            (this as BottomSheetDialog).behavior.isDraggable = true
        }
    }


    override fun getTheme(): Int = R.style.FullExpandedRoundedBottomSheetDialog
}