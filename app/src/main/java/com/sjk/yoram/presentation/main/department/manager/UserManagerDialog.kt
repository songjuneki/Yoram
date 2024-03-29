package com.sjk.yoram.presentation.main.department.manager

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.sjk.yoram.R
import com.sjk.yoram.databinding.DialogUserManagerBinding
import com.sjk.yoram.presentation.main.MainViewModel
import com.sjk.yoram.presentation.main.department.FragDptmentViewModel

class UserManagerDialog: DialogFragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: FragDptmentViewModel by viewModels(ownerProducer = { requireParentFragment().childFragmentManager.fragments.first() })
    private lateinit var binding: DialogUserManagerBinding

    private lateinit var userManagerFragmentManager: FragmentManager
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogUserManagerBinding.inflate(layoutInflater)

        binding.mainVM = mainViewModel
        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        userManagerFragmentManager = childFragmentManager
        navHostFragment = userManagerFragmentManager.findFragmentById(R.id.dialog_user_manager_frame) as NavHostFragment
        navController = navHostFragment.navController

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userManagerCloseEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                dismiss()
            }
        }

        viewModel.userManagerFragEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                navController.navigate(it)
            }
        }

    }

    override fun onResume() {
        super.onResume()

        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val dm = Resources.getSystem().displayMetrics
        val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }

        params?.width = (rect.width() * 0.9).toInt()

        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            this.setOnKeyListener { _, input, keyEvent ->
                if (navController.currentDestination?.id == R.id.userManagerHome) {
                    if (input == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP)
                        findNavController().navigateUp()
                    return@setOnKeyListener false
                } else {
                    if (input == KeyEvent.KEYCODE_DEL && keyEvent.action == KeyEvent.ACTION_DOWN)
                        return@setOnKeyListener false
                    if (input == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                        navController.navigateUp()
                        return@setOnKeyListener true
                    }
                    return@setOnKeyListener false
                }
            }
        }
    }

}