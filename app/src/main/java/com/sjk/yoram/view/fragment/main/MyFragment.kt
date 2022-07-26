package com.sjk.yoram.view.fragment.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.transform.CircleCropTransformation
import com.sjk.yoram.view.activity.LoginActivity
import com.sjk.yoram.viewmodel.MainViewModel
import com.sjk.yoram.model.LoginState
import com.sjk.yoram.model.MyRetrofit
import com.sjk.yoram.model.UserPermission
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragMyBinding
import com.sjk.yoram.model.MyLoginData
import com.sjk.yoram.viewmodel.FragMyViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFragment: Fragment() {
    // private val binding by lazy { FragMyBinding.inflate(layoutInflater) }
    private lateinit var binding: FragMyBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var viewModel: FragMyViewModel

    private lateinit var loginResult: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val title = requireArguments().getString("title")
        //Log.d("jk", "${title} 오픈")
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_my, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(FragMyViewModel::class.java)
        binding.vm = viewModel

        loginResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data!!
                // TODO : 로그인 액티비티에서 로그인 한 정보 가져와서 SharedPreference에 저장
                val sharedPref = requireActivity().getSharedPreferences(getString(R.string.YORAM_LOCAL_PREF), Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                val id = data.getIntExtra("LOGIN_ID", -1)
                val pw = data.getStringExtra("LOGIN_PW")
                editor.putInt(getString(R.string.YORAM_LOCAL_PREF_MYID), id)
                editor.putString(getString(R.string.YORAM_LOCAL_PREF_MYPW), pw)
                editor.commit()
                Log.d("JKJK", "from Login : id=${id}, pw=${pw}")
                CoroutineScope(Dispatchers.Main).launch {
                    mainViewModel.loginData.value = MyRetrofit.userApi.getMyInfo(id)
                    mainViewModel.loginState.value = LoginState.LOGIN
                }
            } else if (it.resultCode == Activity.RESULT_CANCELED) {
                // 로그인 액티비티 취소 동작
            }
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragMyInfoLayout.setOnClickListener{ _ ->
            if (mainViewModel.loginState.value != LoginState.LOGIN) {
                // TODO : loginState != LOGIN 일때 로그인액티비티 오픈
                val loginIntent = Intent(requireContext(), LoginActivity::class.java)
                loginResult.launch(loginIntent)
            } else {
                // TODO : loginState == LOGIN 일때 내 계정 정보 액티비티 오픈
            }
        }
        binding.fragMyLoginMenusOut.setOnClickListener {
            userLogout()
        }

        mainViewModel.loginState.observe(viewLifecycleOwner) {
            when(it) {
                LoginState.LOGIN -> {showUserMenus(true)}
                else -> {showUserMenus(false)}
            }
            showAdminMenus()
        }

        mainViewModel.loginData.observe(viewLifecycleOwner) {
            viewModel.setMyInfo(it)
        }
        viewModel.myInfo.observe(viewLifecycleOwner) {
            binding.fragMyName.text = viewModel.getMyFullName()
            binding.fragMyDepartment.text = viewModel.getMyDptName()
            binding.fragMyAvatar.load("http://3.39.51.49:8080/api/user/avatar?id=${it.id}") {
                crossfade(true)
                transformations(CircleCropTransformation())
            }
        }
    }

    private fun userLogout() {
        val sharedPref = requireActivity().getSharedPreferences(getString(R.string.YORAM_LOCAL_PREF), Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt(getString(R.string.YORAM_LOCAL_PREF_MYID), -1)
        editor.putString(getString(R.string.YORAM_LOCAL_PREF_MYPW), "@")
        editor.commit()

        mainViewModel.loginData.value = MyLoginData()
        mainViewModel.loginState.value = LoginState.NONE
    }

    private fun showUserMenus(show: Boolean) {
        if (show) {
            binding.fragMyLoginMenus.visibility = View.VISIBLE
            binding.fragMyLoginHelp.visibility = View.GONE
        } else {
            binding.fragMyLoginMenus.visibility = View.GONE
            binding.fragMyLoginHelp.visibility = View.VISIBLE
        }
    }

    private fun showAdminMenus() {
        Log.d("JKJK", "mypermission = ${mainViewModel.getUserPermission()}")
        when(mainViewModel.getUserPermission()) {
            UserPermission.ADMIN, UserPermission.SUPER_ADMIN -> binding.fragMyAdminMenus.visibility = View.VISIBLE
            else -> binding.fragMyAdminMenus.visibility = View.GONE
        }
    }

    companion object {
        fun newInstance(title:String) = MyFragment().apply {
            arguments = Bundle().apply {
                putString("title", title)
            }
        }
    }

}