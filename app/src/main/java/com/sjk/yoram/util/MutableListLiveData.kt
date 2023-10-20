package com.sjk.yoram.util

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
        val current = mutableListOf<T>()
        value = current
    }

    fun notifyChange() {
        val current = value
        value = current
    }

    fun toList(): List<T> {
        if (value == null)
            return listOf()
        return value!!.toList()
    }

    fun set(index: Int, item: T) {
        val current = value
        current!![index] = item
        value = current
    }

    fun isListEquals(other: List<T>): Boolean {
        if (value == null) return false
        if (value!!.size != other.size) return false
        value?.forEachIndexed { index, t ->
            if (value!![index]?.equals(other[index]) == false)
                return false
        }
        return true
    }

    fun isEmpty(): Boolean {
        if (value == null) return true
        return value!!.isEmpty()
    }
}