package com.sjk.yoram.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjk.yoram.Model.Department
import com.sjk.yoram.Model.DepartmentV2
import com.sjk.yoram.Model.DptButtonType
import com.sjk.yoram.Model.MyRetrofit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.internal.notify

class FragDptmentViewModel: ViewModel() {
    val departments = MutableLiveData<MutableList<Department>>()
    private val _departments = mutableListOf<Department>()
    var dptFetched = false

    private val _dptSortType = MutableLiveData(DptButtonType.DEPARTMENT)
    val dptSortType: LiveData<DptButtonType> = _dptSortType

    fun loadAllDepartmentsByDpt() {
        this._departments.clear()
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
        }
    }

    fun loadAllDepartmentsByPos() {
        this._departments.clear()
        viewModelScope.launch {

        }
    }

    fun loadAllDepartmentsByName() {
        this._departments.clear()
        viewModelScope.launch {

        }
    }

    fun expandDepartment(dptCode: Int) {
        dptFetched = true
        _departments.forEach {
            if (it.code == dptCode)
                it.isExpanded = !it.isExpanded
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
            Log.d("JKJK", "code=$code, charIndex=$charIndex, pos=$pos, finder = $finder")
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