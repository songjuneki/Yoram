package com.sjk.yoram.view.activity

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import com.sjk.yoram.model.FragmentType
import com.sjk.yoram.R
import com.sjk.yoram.databinding.ActivityMainBinding
import com.sjk.yoram.viewmodel.MainViewModel
import com.sjk.yoram.model.LoginState
import com.sjk.yoram.model.MyRetrofit
import com.sjk.yoram.view.fragment.FragmentFactoryImpl
import com.sjk.yoram.view.fragment.main.*
import com.sjk.yoram.viewmodel.FragHomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this, MainViewModel.Factory(application))[MainViewModel::class.java] }
//    private val homeFragViewModel: FragHomeViewModel by lazy { ViewModelProvider(this, FragHomeViewModel.Factory(application))[FragHomeViewModel::class.java] }
    private lateinit var homeFragViewModel: FragHomeViewModel
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.vm = viewModel
//        binding.lifecycleOwner = this

        homeFragViewModel = ViewModelProvider(this, FragHomeViewModel.Factory(application))[FragHomeViewModel::class.java]

        if (savedInstanceState == null) {
            binding.bottomNavi.selectedItemId = R.id.navi_home
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<HomeFragment>(R.id.mainFrame, "home")
            }
        }

//        CoroutineScope(Dispatchers.Main).launch {
//            val myintent = intent
//            val id = myintent.getIntExtra("loginID", -1)
//            if (id != -1) {
//                viewModel.loginData.value = MyRetrofit.userApi.getMyUserInfo(id)
//                viewModel.loginState.value = LoginState.LOGIN
//            }
//        }

        viewModel.currentFragment.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                when (it) {
                    FragmentType.Fragment_HOME -> {
                        binding.bottomNavi.selectedItemId = R.id.navi_home
                        supportFragmentManager.commit {
                            replace<HomeFragment>(R.id.mainFrame, "home")
                            setReorderingAllowed(true)
                        }
                    }
                    FragmentType.Fragment_ID -> {
                        binding.bottomNavi.selectedItemId = R.id.navi_id
                        supportFragmentManager.commit {
                            replace<IDFragment>(R.id.mainFrame, "id")
                            setReorderingAllowed(true)
                        }
                    }
                    FragmentType.Fragment_DPTMENT -> {
                        binding.bottomNavi.selectedItemId = R.id.navi_dptment
                        supportFragmentManager.commit {
                            replace<DptmentFragment>(R.id.mainFrame, "dptment")
                            setReorderingAllowed(true)
                        }
                    }
                    FragmentType.Fragment_BOARD -> {
                        binding.bottomNavi.selectedItemId = R.id.navi_board
                        supportFragmentManager.commit {
                            replace<BoardFragment>(R.id.mainFrame, "board")
                            setReorderingAllowed(true)
                        }
                    }
                    FragmentType.Fragment_MY -> {
                        binding.bottomNavi.selectedItemId = R.id.navi_my
                        supportFragmentManager.commit {
                            replace<MyFragment>(R.id.mainFrame, "my")
                            setReorderingAllowed(true)
                        }
                    }
                }
            }
        }
    }

    private fun changeFragment(fragmentType: FragmentType) {
        val transaction = supportFragmentManager.beginTransaction()
        var targetFragment = supportFragmentManager.findFragmentByTag(fragmentType.tag)

        if (targetFragment == null) {
            targetFragment = getFragment(fragmentType)
            transaction.add(R.id.mainFrame, targetFragment, fragmentType.tag)
        }
        transaction.show(targetFragment)

        FragmentType.values()
            .filterNot { it == fragmentType }
            .forEach { type ->
                supportFragmentManager.findFragmentByTag(type.tag)?.let {
                    transaction.hide(it)
                }
            }
        transaction.commitAllowingStateLoss()
//        dptFragViewModel.isMoved.value = false
    }


    private fun getFragment(fragmentType: FragmentType): Fragment {
        return when(fragmentType) {
            FragmentType.Fragment_HOME -> HomeFragment()
            FragmentType.Fragment_DPTMENT -> DptmentFragment()
            FragmentType.Fragment_ID -> IDFragment()
            FragmentType.Fragment_BOARD -> BoardFragment()
            FragmentType.Fragment_MY -> MyFragment()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) { //거부
                Toast.makeText(this@MainActivity, "카메라 권한 거부", Toast.LENGTH_SHORT).show()
            }
        }
    }
}