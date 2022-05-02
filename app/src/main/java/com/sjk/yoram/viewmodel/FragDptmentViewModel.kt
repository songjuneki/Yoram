package com.sjk.yoram.viewmodel

import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjk.yoram.Model.Department
import com.sjk.yoram.Model.DptButtonType
import com.sjk.yoram.Model.MyRetrofit
import com.sjk.yoram.Model.dto.Position
import io.github.bangjunyoung.KoreanTextMatch
import io.github.bangjunyoung.KoreanTextMatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FragDptmentViewModel: ViewModel() {
    val departments = MutableLiveData<MutableList<Department>>()
    private val _departments = mutableListOf<Department>()
    var dptFetched = MutableLiveData<Boolean>(false)

    val searchResult = MutableLiveData<MutableList<Department>>()
    private val _searchResult = mutableListOf<Department>()
    val isSearch = MutableLiveData<Boolean>(false)

    private val _dptSortType = MutableLiveData(DptButtonType.DEPARTMENT)
    val dptSortType: LiveData<DptButtonType> = _dptSortType

    val isMoved = MutableLiveData<Boolean>(false)

    fun clearData() {
        _departments.clear()
        departments.value = _departments
        dptFetched.value = false
    }

    fun loadAllDepartmentsByDpt() {
        viewModelScope.launch {
            val topList = MyRetrofit.getMyApi().getAllTopDepartments()
            val childList = MyRetrofit.getMyApi().getAllChildDepartments()
            topList.forEach { this@FragDptmentViewModel._departments.add(Department(it)) }
            childList.forEach { if (it.parent != 0) this@FragDptmentViewModel._departments.add(Department(it)) }
            departments.postValue(_departments)
            dptFetched.value = true
        }
    }

    fun loadAllDepartmentsByPos() {
        viewModelScope.launch {
            val parent = MyRetrofit.getMyApi().getAllParentPositions()
            val childs = MyRetrofit.getMyApi().getAllChildPositions()
            parent.forEach { this@FragDptmentViewModel._departments.add(Department(it)) }
            childs.forEach { if (it.cat != it.code) this@FragDptmentViewModel._departments.add(Department(it)) }
            departments.postValue(_departments)
            dptFetched.value = true
        }
    }

    fun loadAllDepartmentsByName() {
        viewModelScope.launch {

        }
    }

    fun expandDepartment(dptCode: Int) {
        _departments.forEach {
            if (it.code == dptCode)
                it.isExpanded = !it.isExpanded
        }
        departments.postValue(_departments)
    }

    fun expandSearchDepartment(dptCode: Int) {
        _searchResult.forEach {
            if (it.code == dptCode)
                it.isExpanded = !it.isExpanded
        }
        searchResult.postValue(_searchResult)
    }

    fun collapseAllDepartment() {
        _departments.forEach {
            it.isExpanded = false
        }
        departments.postValue(_departments)
    }

    fun searchDptName(str: String) {
        viewModelScope.async {
            this@FragDptmentViewModel._searchResult.clear()
            if (str.isDigitsOnly()) {

            } else {
                val matcher = KoreanTextMatcher(str)
                this@FragDptmentViewModel._departments.reversed().forEach { dpt ->
                    val searched = dpt.users.filter { matcher.match(it.fname+it.lname).success() }.toMutableList()

                    if (searched.isNotEmpty() || _searchResult.filter { it.parentCode == dpt.code }.isNotEmpty()) {
                        val existDpt = Department(dpt.parentCode, dpt.name, dpt.code)
                        existDpt.users.addAll(searched)
                        existDpt.isExpanded = true
                        _searchResult.add(existDpt)
                    }
                }
            }
            _searchResult.reverse()
            this@FragDptmentViewModel.searchResult.postValue(_searchResult)
            this@FragDptmentViewModel.isSearch.value = true
        }
    }


    fun setSortType(type: DptButtonType) {
        if (dptSortType.value == type) return
        _dptSortType.value = type
    }

    fun MutableList<Department>.expand(code: Int): Boolean {
        val strCode = code.toString()
        var isSuccess = false
        var charIndex = 0
        var pos = 100000
        var finder = strCode[charIndex++].digitToInt() * pos
        var iterator = this.find { it.code == finder }

        do {
            if (code == iterator!!.code)
                isSuccess = true
            finder = strCode[charIndex++].digitToInt() * pos + finder
            pos /= 10
            iterator = iterator!!.childDepartment.find { it.code == finder }
            if (code == iterator!!.code)
                isSuccess = true
        } while(!isSuccess)

        return if (iterator != null) {
            iterator!!.isExpanded = !iterator!!.isExpanded
            true
        } else
            false
    }

}