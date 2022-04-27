package com.sjk.yoram.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjk.yoram.Model.Department
import com.sjk.yoram.Model.DptButtonType
import com.sjk.yoram.Model.MyRetrofit
import com.sjk.yoram.Model.dto.Position
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

    fun collapseAllDepartment() {
        _departments.forEach {
            it.isExpanded = false
        }
        departments.postValue(_departments)
    }

    fun searchDpt(str: String) {

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