package com.sjk.yoram

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sjk.yoram.Model.DptButtonType
import com.sjk.yoram.Model.FragmentType

class MainVM: ViewModel() {

    private val _currentFragmentType = MutableLiveData(FragmentType.Fragment_HOME)
    val currentFragmentType: LiveData<FragmentType> = _currentFragmentType

    fun setCurrentFragment(menuItemId: Int): Boolean {
        val fragType = getFragmentType(menuItemId)
        changeCurrentFragment(fragType)
        return true
    }

    fun moveDptFrag() {
        changeCurrentFragment(FragmentType.Fragment_DPTMENT)
    }


    private fun getFragmentType(menuItemId: Int): FragmentType {
        return when(menuItemId) {
            R.id.navi_home -> FragmentType.Fragment_HOME
            R.id.navi_dptment -> FragmentType.Fragment_DPTMENT
            R.id.navi_id -> FragmentType.Fragment_ID
            R.id.navi_board -> FragmentType.Fragment_BOARD
            R.id.navi_my -> FragmentType.Fragment_MY
            else -> throw IllegalArgumentException("not found menu item id")
        }
    }

    private fun changeCurrentFragment(fragmentType: FragmentType) {
        if (currentFragmentType.value == fragmentType) return

        _currentFragmentType.value = fragmentType
    }
}