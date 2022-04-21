package com.sjk.yoram.viewmodel

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sjk.yoram.Model.BannerData

open class CardMainBannerViewModel(data: BannerData): ViewModel() {
    private val _currentValue = MutableLiveData(data)
    val currentValue: LiveData<BannerData> get() = _currentValue

    init {
    }

    fun updateValue() {

    }

    fun clickImg(url: String) {

    }
}