package com.sjk.yoram.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.sjk.yoram.model.MutableListLiveData
import com.sjk.yoram.model.dto.Banner
import com.sjk.yoram.model.ui.adapter.AdminBannerListAdapter
import com.sjk.yoram.repository.ServerRepository
import com.sjk.yoram.repository.UserRepository
import kotlinx.coroutines.launch

class AdminBannerViewModel(serverRepository: ServerRepository, userRepository: UserRepository): ViewModel() {
    private lateinit var _immutableBanners: List<Banner>
    val banners = MutableListLiveData<Banner>()

    val adapter: AdminBannerListAdapter = AdminBannerListAdapter()

    init {
        viewModelScope.launch {
            _immutableBanners = serverRepository.getAllBanners(true).toList()
            banners.value = _immutableBanners.toMutableList()
        }
    }

    fun deleteBanner(pos: Int) {
        banners.value?.removeAt(pos)
    }

    fun insertNewBanner(item: Banner) {
        banners.value?.add(item)
    }

    fun isEditedBanners(): Boolean {
        banners.value?.let {
            return _immutableBanners != it
        }
        return false
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdminBannerViewModel(ServerRepository.getInstance(application)!!, UserRepository.getInstance(application)!!) as T
        }
    }
}