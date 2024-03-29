package com.sjk.yoram.presentation.main.my

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.navGraphViewModels
import com.sjk.yoram.R
import com.sjk.yoram.presentation.main.MainViewModel
import com.sjk.yoram.databinding.FragMyBinding
import com.sjk.yoram.presentation.main.my.attend.AttendActivity
import com.sjk.yoram.presentation.main.my.edit.EditActivity
import com.sjk.yoram.presentation.main.my.give.GiveActivity
import com.sjk.yoram.presentation.main.my.preference.PreferenceActivity

class MyFragment: Fragment() {
    private lateinit var binding: FragMyBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: FragMyViewModel by navGraphViewModels(R.id.navi_my) { FragMyViewModel.Factory(requireActivity().application) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_my, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.mainVM = mainViewModel
        binding.vm = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.editEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                val intent = Intent(context, EditActivity::class.java)
                this.myEditResult.launch(intent)
                activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

        viewModel.giveEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                val intent = Intent(context, GiveActivity::class.java)
                this.myGiveResult.launch(intent)
                activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

        viewModel.attendEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                val intent = Intent(context, AttendActivity::class.java)
                context?.startActivity(intent)
                activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

        viewModel.prefEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                val intent = Intent(context, PreferenceActivity::class.java)
                this.myPrefResult.launch(intent)
                activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
    }

    private val myEditResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            mainViewModel.loadLoginData()
        }
    }

    private val myGiveResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            mainViewModel.loadGiveAmount()
        }
    }

    private val myPrefResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                mainViewModel.loadLoginData()
            }
            PreferenceActivity.RESULT_LOGOUT -> {
                mainViewModel.logout()
                mainViewModel.loadLoginData()
            }
            else -> {

            }
        }
    }

    override fun onResume() {
        mainViewModel.loadLoginData()
        mainViewModel.loadGiveAmount()
        super.onResume()
    }
}