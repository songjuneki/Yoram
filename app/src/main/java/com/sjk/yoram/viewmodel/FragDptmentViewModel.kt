package com.sjk.yoram.viewmodel

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.sjk.yoram.Model.Department
import com.sjk.yoram.Model.DptButtonType
import com.sjk.yoram.Model.MyRetrofit
import com.sjk.yoram.Model.dto.SimpleUser
import io.github.bangjunyoung.KoreanTextMatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class FragDptmentViewModel: ViewModel() {
    val departments = MutableLiveData<MutableList<Department>>()
    private val _departments = mutableListOf<Department>()
    var dptFetched = MutableLiveData<Boolean>(false)

    val users = MutableLiveData<MutableList<SimpleUser>>()
    private var _users = mutableListOf<SimpleUser>()
    var userFetched = MutableLiveData<Boolean>(false)

    val dptSearchResult = MutableLiveData<MutableList<Department>>()
    private val _dptSearchResult = mutableListOf<Department>()
    val searchResult = MutableLiveData<MutableList<SimpleUser>>()
    private var _searchResult = mutableListOf<SimpleUser>()
    val isSearch = MutableLiveData<Boolean>(false)

    private val _dptSortType = MutableLiveData(DptButtonType.DEPARTMENT)
    val dptSortType: LiveData<DptButtonType> = _dptSortType

    val isMoved = MutableLiveData<Boolean>(false)
    var recycler: RecyclerView? = null


    fun clearData() {
        _departments.clear()
        _users.clear()
        departments.value = _departments
        users.value = _users
        dptFetched.value = false
        userFetched.value = false
    }

    suspend fun loadAllDepartmentsByDpt() =
        viewModelScope.async {
            val topList = MyRetrofit.getMyApi().getAllTopDepartments()
            val childList = MyRetrofit.getMyApi().getAllChildDepartments()
            topList.forEach { this@FragDptmentViewModel._departments.add(Department(it)) }
            childList.forEach { if (it.parent != 0) this@FragDptmentViewModel._departments.add(Department(it)) }
            departments.postValue(_departments)
            dptFetched.value = true
            userFetched.value = false
        }


    suspend fun loadAllDepartmentsByPos() =
        viewModelScope.async {
            val parent = MyRetrofit.getMyApi().getAllParentPositions()
            val childs = MyRetrofit.getMyApi().getAllChildPositions()
            parent.forEach { this@FragDptmentViewModel._departments.add(Department(it)) }
            childs.forEach { if (it.cat != it.code) this@FragDptmentViewModel._departments.add(Department(it)) }
            departments.postValue(_departments)
            dptFetched.value = true
            userFetched.value = false
        }


    fun loadAllDepartmentsByName() {
        viewModelScope.async {
            this@FragDptmentViewModel._users = MyRetrofit.getMyApi().getAllSimpleUsersByName()
            users.postValue(_users)
            dptFetched.value = false
            userFetched.value = true
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
        _dptSearchResult.forEach {
            if (it.code == dptCode)
                it.isExpanded = !it.isExpanded
        }
        dptSearchResult.postValue(_dptSearchResult)
    }

    fun collapseAllDepartment() {
        _departments.forEach {
            it.isExpanded = false
        }
        departments.postValue(_departments)
    }

    fun sortAndExpandDepartment(type: DptButtonType, dptCode: Int) {
        viewModelScope.async {
            if (dptSortType.value == type) {
                collapseAllDepartment()
                expandDepartment(dptCode)
            } else {
                setSortType(type)
                delay(1000L)
                expandDepartment(dptCode)
            }
            recycler!!.adapter!!.notifyDataSetChanged()
        }
    }

    fun searchDptName(str: String) {
        viewModelScope.async {
            this@FragDptmentViewModel._dptSearchResult.clear()
            if (str.isDigitsOnly()) {

            } else {
                val matcher = KoreanTextMatcher(str)
                this@FragDptmentViewModel._departments.reversed().forEach { dpt ->
                    val searched = dpt.users.filter { matcher.match(it.fname+it.lname).success() }.toMutableList()

                    if (searched.isNotEmpty() || _dptSearchResult.filter { it.parentCode == dpt.code }.isNotEmpty()) {
                        val existDpt = Department(dpt.parentCode, dpt.name, dpt.code)
                        existDpt.users.addAll(searched)
                        existDpt.isExpanded = true
                        _dptSearchResult.add(existDpt)
                    }
                }
            }
            _dptSearchResult.reverse()
            this@FragDptmentViewModel.dptSearchResult.postValue(_dptSearchResult)
            this@FragDptmentViewModel.isSearch.value = true
        }
    }

    fun searchName(str: String) {
        viewModelScope.async {
            this@FragDptmentViewModel._searchResult.clear()
            if (str.isDigitsOnly()) {

            } else {
                val matcher = KoreanTextMatcher(str)
                val searched = _users.filter { matcher.match(it.fname+it.lname).success() }.toMutableList()
                this@FragDptmentViewModel._searchResult = searched
                this@FragDptmentViewModel.searchResult.postValue(_searchResult)
                this@FragDptmentViewModel.isSearch.value = true
            }
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