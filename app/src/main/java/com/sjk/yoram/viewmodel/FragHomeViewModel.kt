package com.sjk.yoram.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjk.yoram.model.*
import kotlinx.coroutines.launch

open class FragHomeViewModel: ViewModel() {
    //var cards = ObservableArrayList<Card>()
    val cards = MutableLiveData<MutableList<Card>>()
    private val _cards = mutableListOf<Card>()

    val rootDpts = MutableLiveData<MutableList<SimpleDpt>>()
    private val _rootDpts = mutableListOf<SimpleDpt>()

    fun loadRootDpts() {
        viewModelScope.launch {
            val dpts = MyRetrofit.getMyApi().getAllTopDepartments()
            dpts.forEach {
                _rootDpts.add(SimpleDpt(it.code, it.name))
            }
            rootDpts.value = _rootDpts
        }
    }

    fun addCard(card: Card) {
        _cards.add(card)
        cards.value = _cards
    }

    fun modifyCard(pos: Int, card: Card) {
        _cards[pos] = card
        cards.value = _cards
    }
}