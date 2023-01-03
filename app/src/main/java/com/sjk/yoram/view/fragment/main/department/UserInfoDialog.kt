package com.sjk.yoram.view.fragment.main.department

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sjk.yoram.R
import com.sjk.yoram.databinding.DialogUserInfoBinding
import com.sjk.yoram.viewmodel.FragDptmentViewModel
import com.sjk.yoram.viewmodel.MainViewModel

class UserInfoDialog: BottomSheetDialogFragment() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var dptViewModel: FragDptmentViewModel
    private lateinit var binding: DialogUserInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        dptViewModel = ViewModelProvider(requireActivity())[FragDptmentViewModel::class.java]
        binding = DialogUserInfoBinding.inflate(layoutInflater)
        binding.vm = dptViewModel
        binding.mainVM = mainViewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        initView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dptViewModel.userCallEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
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

        dptViewModel.userManageEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(R.id.action_userInfoDialog_to_userManagerDialog)
            }
        }
    }

    private fun initView() {
        binding.dialogUserInfoClose.setOnClickListener { dismiss() }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            (this as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
            this.behavior.isDraggable = true
        }
    }

    override fun getTheme(): Int = R.style.FullExpandedRoundedBottomSheetDialog
}