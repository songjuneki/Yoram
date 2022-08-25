package com.sjk.yoram.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.sjk.yoram.model.*
import com.sjk.yoram.model.dto.Banner
import com.sjk.yoram.repository.ServerRepository
import com.sjk.yoram.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FragHomeViewModel(private val userRepository: UserRepository, private val serverRepository: ServerRepository): ViewModel() {
    //var cards = ObservableArrayList<Card>()
    val cards = MutableLiveData<MutableList<Card>>()
    private val _cards = mutableListOf<Card>()

    val rootDpts = MutableLiveData<MutableList<SimpleDpt>>()
    private val _rootDpts = mutableListOf<SimpleDpt>()

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

    private fun loadBanners() = viewModelScope.launch {
        _bannerList.addAll(serverRepository.getAllBanner().toMutableList())
    }

    private fun loadServerValues() = viewModelScope.async {
        _maxWeek.value = serverRepository.getMaxWeekOfMonth()
    }


    fun addCard(card: Card) {
        _cards.add(card)
        cards.value = _cards
    }

    fun modifyCard(pos: Int, card: Card) {
        _cards[pos] = card
        cards.value = _cards
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FragHomeViewModel(UserRepository.getInstance(application)!!, ServerRepository.getInstance(application)!!) as T
        }
    }
}