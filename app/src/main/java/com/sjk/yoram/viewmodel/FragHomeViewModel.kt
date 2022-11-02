package com.sjk.yoram.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.sjk.yoram.model.*
import com.sjk.yoram.model.dto.Banner
import com.sjk.yoram.repository.ServerRepository
import com.sjk.yoram.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FragHomeViewModel(private val userRepository: UserRepository, private val serverRepository: ServerRepository): ViewModel() {
    private val _bannerList = MutableListLiveData<Banner>()
    val bannerList: LiveData<MutableList<Banner>>
        get() = _bannerList

    private val _maxWeek = MutableLiveData<Int>()
    val maxWeek: LiveData<Int>
        get() = _maxWeek

    init {
        loadBanners()
        loadServerValues()
    }

    fun loadBanners() = viewModelScope.launch {
        _bannerList.value = serverRepository.getAllBanners().toMutableList()
    }

    private fun loadServerValues() = viewModelScope.launch {
        _maxWeek.value = serverRepository.getMaxWeekOfMonth()
    }



    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FragHomeViewModel(UserRepository.getInstance(application)!!, ServerRepository.getInstance(application)!!) as T
        }
    }
}