package com.sjk.yoram.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjk.yoram.Model.Department
import com.sjk.yoram.Model.DptButtonType
import com.sjk.yoram.Model.MyRetrofit
import kotlinx.coroutines.launch

class FragDptmentViewModel: ViewModel() {
    val departments = MutableLiveData<MutableList<Department>>()
    private val _departments = mutableListOf<Department>()
    var dptFetched = MutableLiveData<Boolean>()

    val positons = MutableLiveData<MutableList<Department>>()
    private val _positions= mutableListOf<Department>()

    private val _dptSortType = MutableLiveData(DptButtonType.DEPARTMENT)
    val dptSortType: LiveData<DptButtonType> = _dptSortType

    fun clearData(type: DptButtonType) {
        when(type) {
            DptButtonType.DEPARTMENT -> this._departments.clear()
            DptButtonType.POSITION -> this._positions.clear()
            DptButtonType.NAME -> this._positions.clear()
        }
        dptFetched.value = false
    }

    fun loadAllDepartmentsByDpt() {
        viewModelScope.launch {
            val topList = MyRetrofit.getMyApi().getAllTopDepartments()
            val childList = MyRetrofit.getMyApi().getAllChildDepartments()
            topList.forEach {
                this@FragDptmentViewModel._departments.add(Department(it))
            }
            childList.forEach {
                if (it.parent != 0)
                    this@FragDptmentViewModel._departments.add(Department(it))
            }
            departments.postValue(_departments)
            dptFetched.value = true
        }
    }

    fun loadAllDepartmentsByPos() {
        this._positions.clear()
        viewModelScope.launch {

        }
    }

    fun loadAllDepartmentsByName() {
        this._departments.clear()
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