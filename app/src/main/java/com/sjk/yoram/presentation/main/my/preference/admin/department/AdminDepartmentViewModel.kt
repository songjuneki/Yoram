package com.sjk.yoram.presentation.main.my.preference.admin.department

import android.app.Application
import android.content.DialogInterface
import androidx.lifecycle.*
import com.sjk.yoram.R
import com.sjk.yoram.data.entity.Department
import com.sjk.yoram.util.Event
import com.sjk.yoram.util.MutableListLiveData
import com.sjk.yoram.presentation.common.listener.AdminDepartmentClickListener
import com.sjk.yoram.data.repository.DepartmentRepository
import com.sjk.yoram.data.repository.UserRepository
import kotlinx.coroutines.launch

class AdminDepartmentViewModel(private val userRepository: UserRepository, private val departmentRepository: DepartmentRepository): ViewModel() {
    private val originalDepartmentList: MutableList<Department> = mutableListOf()

    val editableDepartmentList = MutableListLiveData<Department>()

    private val clickListener: AdminDepartmentClickListener = object: AdminDepartmentClickListener {
        override fun onAddClick(code: Int) {
            openAddDepartmentDialog(parent = code)
        }

        override fun onEditClick(code: Int) {
            openEditDepartmentDialog(code = code)
        }
    }
    val adapter = AdminDepartmentListAdapter(clickListener)

    val detailDepartment = MutableLiveData<Department>()

    private val _backEvent = MutableLiveData<Event<Unit>>()
    val backEvent: LiveData<Event<Unit>>
        get() = _backEvent

    private val _detailType = MutableLiveData<DepartmentDetailType>()
    val detailType: LiveData<DepartmentDetailType>
        get() = _detailType

    val selectedIndexParentDepartment = MutableLiveData<Int>(0)

    private val _detailDepartmentEvent = MutableLiveData<Event<Unit>>()
    val detailDepartmentEvent: LiveData<Event<Unit>>
        get() = _detailDepartmentEvent

    private val _applyDetailEditEvent = MutableLiveData<Event<Unit>>()
    val applyDetailEditEvent: LiveData<Event<Unit>>
        get() = _applyDetailEditEvent

    private val _deleteEvent = MutableLiveData<Event<DialogInterface.OnClickListener>>()
    val deleteEvent: LiveData<Event<DialogInterface.OnClickListener>>
        get() = _deleteEvent

    private val _isUsingDepartment = MutableLiveData<Event<Unit>>()
    val isUsingDepartment: LiveData<Event<Unit>>
        get() = _isUsingDepartment

    private val _isChanged = MutableLiveData<Boolean>(false)
    val isChanged: LiveData<Boolean>
        get() = _isChanged

    private val _applyEvent = MutableLiveData<Event<Unit>>()
    val applyEvent: LiveData<Event<Unit>>
        get() = _applyEvent

    private val _exitEvent = MutableLiveData<Event<Unit>>()
    val exitEvent: LiveData<Event<Unit>>
        get() = _exitEvent

    private val _applyFailEvent = MutableLiveData<Event<Unit>>()
    val applyFailEvent: LiveData<Event<Unit>>
        get() = _applyFailEvent

    init {
        viewModelScope.launch {
            if (userRepository.getMyPermission(userRepository.getLoginID()) > 2)
                loadDepartmentList()
        }
    }

    fun btnEvent(btnId: Int) {
        when (btnId) {
            R.id.frag_my_pref_admin_department_header_add -> openAddDepartmentDialog(0)
            R.id.dialog_my_admin_department_edit_delete -> checkAndDeleteDepartment()
            R.id.frag_my_pref_admin_department_done -> _applyEvent.value = Event(Unit)
        }
    }

    fun loadDepartmentList() {
        viewModelScope.launch {
            originalDepartmentList.clear()
            originalDepartmentList.addAll(departmentRepository.getDtoDepartmentList())
            editableDepartmentList.value?.clear()
            editableDepartmentList.value = departmentRepository.getDtoDepartmentList().toMutableList()
            adapter.notifyDataSetChanged()
            _isChanged.value = false
        }
    }

    fun getDepartmentName(code: Int): String {
        if (code == 0) return "최상위 부서"
        return editableDepartmentList.value?.find { it.code == code }?.name ?: "부서"
    }

    fun applyDetailEditAction() {
        when (_detailType.value) {
            DepartmentDetailType.ADD -> addDepartment()
            DepartmentDetailType.EDIT -> editDepartment()
            else -> return
        }
        selectedIndexParentDepartment.value = 0
        _applyDetailEditEvent.value = Event(Unit)
    }


    private fun openEditDepartmentDialog(code: Int) {
        _detailType.value = DepartmentDetailType.EDIT

        val department = editableDepartmentList.value?.find { it.code == code }
        detailDepartment.value = department
        selectedIndexParentDepartment.value = if (department?.parent == 0) 0 else editableDepartmentList.toList().indexOfFirst { it.code == department?.parent } + 1
        _detailDepartmentEvent.value = Event(Unit)
    }

    private fun openAddDepartmentDialog(parent: Int) {
        _detailType.value = DepartmentDetailType.ADD

        var maxCode = editableDepartmentList.value?.maxByOrNull { it.code }?.code ?: 0
        maxCode += 1

        detailDepartment.value = Department(maxCode, "", parent)
        selectedIndexParentDepartment.value = if (parent == 0) 0 else editableDepartmentList.toList().indexOfFirst { it.code == parent } + 1
        _detailDepartmentEvent.value = Event(Unit)
    }

    private fun checkAndDeleteDepartment() {
        viewModelScope.launch {
            val isUsing = departmentRepository.getCheckDepartmentIsUsing(detailDepartment.value?.code ?: -1)
            if (isUsing) _isUsingDepartment.value = Event(Unit)
            else {
                _deleteEvent.value = Event(
                    DialogInterface.OnClickListener { d, _ ->
                        deleteDepartment()
                        d.dismiss()
                        _backEvent.value = Event(Unit)
                    }
                )
            }
        }
    }

    private fun addDepartment() {
        editableDepartmentList.add(detailDepartment.value!!)
        adapter.submitList(editableDepartmentList.toList())
        adapter.notifyDataSetChanged()
        checkChanged()
    }

    private fun editDepartment() {
        val index = editableDepartmentList.value?.indexOfFirst { it.code == detailDepartment.value!!.code } ?: -1
        if (index < 0) return

        editableDepartmentList.set(index, detailDepartment.value!!)
        adapter.notifyDataSetChanged()
        checkChanged()
    }

    private fun deleteDepartment() {
        val index = editableDepartmentList.value?.indexOfFirst { it.code == detailDepartment.value!!.code } ?: -1
        if (index < 0) return

        editableDepartmentList.remove(editableDepartmentList.value!![index])
        adapter.notifyDataSetChanged()
        checkChanged()
    }

    private fun checkChanged() {
        _isChanged.value = !editableDepartmentList.isListEquals(originalDepartmentList)
    }

    fun changedDepartmentListApply() {
        viewModelScope.launch {
            val isSuccess = departmentRepository.uploadDepartmentList(editableDepartmentList.toList())
            if (isSuccess) _exitEvent.value = Event(Unit)
            else _applyFailEvent.value = Event(Unit)
        }
    }


    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdminDepartmentViewModel(UserRepository.getInstance(application)!!, DepartmentRepository.getInstance(application)!!) as T
        }
    }

    enum class DepartmentDetailType { ADD, EDIT }

    fun departmentParentSpinnerSelectedChanged(position: Int) {
        if (position == 0) {
            detailDepartment.value?.parent = 0
            return
        }
        detailDepartment.value?.parent = editableDepartmentList.toList()[position - 1].code
    }

    fun toNameList(dptList: MutableListLiveData<Department>): List<String> {
        val list = mutableListOf<String>()
        list.add("최상위 부서")
        val currentList = dptList.toList().map { it.name }
        list.addAll(currentList)

        return list
    }
}