package com.sjk.yoram.viewmodel

import android.app.Application
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.sjk.yoram.R
import com.sjk.yoram.model.*
import com.sjk.yoram.model.dto.SimpleUser
import com.sjk.yoram.model.dto.UserDetail
import com.sjk.yoram.model.ui.adapter.DepartmentListAdapter
import com.sjk.yoram.model.ui.adapter.SimpleUserListAdapter
import com.sjk.yoram.model.ui.listener.DepartmentItemClickListener
import com.sjk.yoram.model.ui.listener.TextInputChanged
import com.sjk.yoram.model.ui.listener.UserItemClickListener
import com.sjk.yoram.repository.DepartmentRepository
import com.sjk.yoram.repository.ServerRepository
import com.sjk.yoram.repository.UserRepository
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener
import com.skydoves.powerspinner.PowerSpinnerInterface
import com.skydoves.powerspinner.PowerSpinnerView
import io.github.bangjunyoung.KoreanTextMatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FragDptmentViewModel(private val userRepository: UserRepository, private val serverRepository: ServerRepository, private val departmentRepository: DepartmentRepository): ViewModel() {

    private var _myPermission: Int = -5

    private val _dptSortType = MutableLiveData(DptButtonType.NAME)
    val dptSortType: LiveData<DptButtonType> = _dptSortType

    private val _departments = MutableLiveData<MutableList<Department>>()
    val departments: LiveData<MutableList<Department>>
        get() = _departments

    private val _searchResult = MutableLiveData<MutableList<SimpleUser>>()
    val searchResult: LiveData<MutableList<SimpleUser>>
        get() = _searchResult

    private val _detailUser = MutableLiveData<UserDetail>()
    val detailUser: LiveData<UserDetail>
        get() = _detailUser

    private val _userDetailEvent = MutableLiveData<Event<Unit>>()
    val userDetailEvent: LiveData<Event<Unit>>
        get() = _userDetailEvent

    private val _userCallEvent = MutableLiveData<Event<String>>()
    val userCallEvent: LiveData<Event<String>>
        get() = _userCallEvent

    private val _userMsgEvent = MutableLiveData<Event<String>>()
    val userMsgEvent: LiveData<Event<String>>
        get() = _userMsgEvent

    val isSearching = MutableLiveData<Boolean>(false)
    val searchKeyword = MutableLiveData<String>("")


    init {
        viewModelScope.launch {
            _myPermission = userRepository.getMyPermission(userRepository.getLoginID())
            _departments.value = mutableListOf()
            _searchResult.value = mutableListOf()
            loadDepartmentByName()
        }
    }

    val sortTypeListener = OnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
        changeDptSortType(newIndex)
    }

    fun clickEvent(btnId: Int) {
        when(btnId) {
            R.id.frag_dptment_header_search -> { if (_myPermission > 0) isSearching.value = true }
            R.id.frag_dptment_header_cancel -> { isSearching.value = false; searchKeyword.value = "" }
        }
    }


    val searchInputChanged = object: TextInputChanged {
        override fun afterTextChanged(view: TextInputLayout, input: String) {
            viewModelScope.async {
                _searchJob?.cancel()
                if (input.isEmpty()) {
                    _searchResult.value = mutableListOf()
                } else {
                    searchUserByName(input)
                }
            }
        }
    }

    private val dptClickListener = object: DepartmentItemClickListener {
        override fun onClick(department: Department) {
            department.isExpanded = !department.isExpanded
        }
    }
    private val userClickListener = object: UserItemClickListener {
        override fun onClick(user: SimpleUser) {
            selectedUser(user.id)
        }
    }

    fun listAdapter() : DepartmentListAdapter = DepartmentListAdapter(_myPermission, dptClickListener, if (_myPermission < 1) null else userClickListener)
    fun searchListAdapter() : SimpleUserListAdapter = SimpleUserListAdapter(userClickListener)

    private fun selectedUser(id: Int) = viewModelScope.launch {
        _detailUser.value = userRepository.getUserDetail(id)
        _userDetailEvent.value = Event(Unit)
    }

    private var _searchJob: Job? = null
    private fun searchUserByName(name: String) {
        _searchJob = viewModelScope.launch {
            _searchResult.value = departmentRepository.searchUserByName(name)
        }
    }

    private fun loadDepartmentByName() = viewModelScope.launch {
        _departments.value = departmentRepository.getAllDepartmentsByName()
    }

    private fun loadDepartmentByDepartment() = viewModelScope.launch {
        _departments.value = departmentRepository.getAllDepartmentsByDepartment()
    }

    private fun loadDepartmentByPosition() = viewModelScope.launch {
        _departments.value = departmentRepository.getDepartmentsByPosition()
    }

    private fun changeDptSortType(index: Int) {
        when (index) {
            0 -> {
                _dptSortType.value = DptButtonType.NAME
                loadDepartmentByName()
            }
            1 -> {
                _dptSortType.value = DptButtonType.DEPARTMENT
                loadDepartmentByDepartment()
            }
            2 -> {
                _dptSortType.value = DptButtonType.POSITION
                loadDepartmentByPosition()
            }
        }
    }

    fun callOnUserDetail(phone: String) {
        _userCallEvent.value = Event(phone)
    }

    fun msgOnUserDetail(phone: String) {
        _userMsgEvent.value = Event(phone)
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FragDptmentViewModel(UserRepository.getInstance(application)!!, ServerRepository.getInstance(application)!!, DepartmentRepository.getInstance(application)!!) as T
        }
    }
}