package com.sjk.yoram.model

import androidx.lifecycle.MutableLiveData

class MutableListLiveData<T>() : MutableLiveData<MutableList<T>>() {
    init {
        value = mutableListOf()
    }

    fun add(item: T) {
        val current = value
        current!!.add(item)
        value = current
    }

    fun addAll(list: List<T>) {
        val current = value
        current!!.addAll(list)
        value = current
    }

    fun remove(item: T) {
        val current = value
        current!!.remove(item)
        value = current
    }

    fun clear() {
        val current = value
        current!!.clear()
        value = current
    }

    fun notifyChange() {
        val current = value
        value = current
    }
}