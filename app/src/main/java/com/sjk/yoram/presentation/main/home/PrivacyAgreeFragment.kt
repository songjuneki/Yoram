package com.sjk.yoram.presentation.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sjk.yoram.R
import com.sjk.yoram.databinding.DialogPrivacyRuleBinding
import com.sjk.yoram.presentation.main.MainViewModel

class PrivacyAgreeFragment: BottomSheetDialogFragment() {
    private lateinit var binding: DialogPrivacyRuleBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogPrivacyRuleBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.vm = viewModel

        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
        (dialog as BottomSheetDialog).behavior.isHideable = false
        (dialog as BottomSheetDialog).behavior.halfExpandedRatio = 0.99999f
        (dialog as BottomSheetDialog).behavior.isDraggable = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.backEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                viewModel.loadLoginData()
                viewModel.checkRuleAgreeExpire()
                findNavController().popBackStack()
            }
        }
    }

    override fun getTheme(): Int = R.style.ExpandedRoundedBottomSheetDialog

}