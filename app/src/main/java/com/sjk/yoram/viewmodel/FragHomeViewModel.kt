package com.sjk.yoram.viewmodel

import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sjk.yoram.Model.BannerData
import com.sjk.yoram.Model.Card
import com.sjk.yoram.Model.cardData

open class FragHomeViewModel: ViewModel() {
    //var cards = ObservableArrayList<Card>()
    val cards = MutableLiveData<MutableList<Card>>()
    private val _cards = mutableListOf<Card>()

    fun addCard(card: Card) {
        _cards.add(card)
        cards.value = _cards
    }
}