package com.sjk.yoram.viewmodel

import android.app.Application
import android.content.DialogInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sjk.yoram.R
import com.sjk.yoram.model.Event
import com.sjk.yoram.model.MutableListLiveData
import com.sjk.yoram.model.dto.Position
import com.sjk.yoram.model.ui.adapter.AdminDepartmentListAdapter
import com.sjk.yoram.model.ui.adapter.AdminPositionListAdapter
import com.sjk.yoram.model.ui.listener.AdminDepartmentClickListener
import com.sjk.yoram.repository.DepartmentRepository
import kotlinx.coroutines.launch
import kotlin.math.max

class AdminPositionViewModel(private val departmentRepository: DepartmentRepository): ViewModel() {
    private val originalPositionList = mutableListOf<Position>()

    val editablePositionList = MutableListLiveData<Position>()

    val detailPosition = MutableLiveData<Position>()

    private val _detailType = MutableLiveData<PositionDetailType>()
    val detailType: LiveData<PositionDetailType>
        get() = _detailType

    private val _isChanged = MutableLiveData(false)
    val isChanged: LiveData<Boolean>
        get() = _isChanged


    private val clickListener = object: AdminDepartmentClickListener {
        override fun onAddClick(code: Int) {
            openAddPositionDialog(parent = code)
        }

        override fun onEditClick(code: Int) {
            openEditPositionDialog(code = code)
        }
    }

    val adapter = AdminPositionListAdapter(clickListener)

    val selectedIndexParentPosition = MutableLiveData(0)

    private val _detailPositionEvent = MutableLiveData<Event<Unit>>()
    val detailPositionEvent: LiveData<Event<Unit>>
        get() = _detailPositionEvent

    private val _applyDetailEditEvent = MutableLiveData<Event<Unit>>()
    val applyDetailEditEvent: LiveData<Event<Unit>>
        get() = _applyDetailEditEvent

    private val _deleteEvent = MutableLiveData<Event<DialogInterface.OnClickListener>>()
    val deleteEvent: LiveData<Event<DialogInterface.OnClickListener>>
        get() = _deleteEvent

    private val _isUsingPosition = MutableLiveData<Event<Unit>>()
    val isUsingPosition: LiveData<Event<Unit>>
        get() = _isUsingPosition

    private val _applyEvent = MutableLiveData<Event<Unit>>()
    val applyEvent: LiveData<Event<Unit>>
        get() = _applyEvent

    private val _backEvent = MutableLiveData<Event<Unit>>()
    val backEvent: LiveData<Event<Unit>>
        get() = _backEvent

    private val _exitEvent = MutableLiveData<Event<Unit>>()
    val exitEvent: LiveData<Event<Unit>>
        get() = _exitEvent

    private val _applyFailEvent = MutableLiveData<Event<Unit>>()
    val applyFailEvent: LiveData<Event<Unit>>
        get() = _applyFailEvent

    fun btnEvent(btnId: Int) {
        when (btnId) {
            R.id.frag_my_pref_admin_position_header_add -> openAddPositionDialog(0)
            R.id.dialog_my_admin_position_edit_delete -> checkAndDeletePosition()
            R.id.dialog_my_admin_position_edit_cancel -> _backEvent.value = Event(Unit)
            R.id.frag_my_pref_admin_position_done -> _applyEvent.value = Event(Unit)
        }
    }

    fun loadPositionList() {
        viewModelScope.launch {
            originalPositionList.clear()
            originalPositionList.addAll(departmentRepository.getDtoPositionList())

            editablePositionList.clear()
            editablePositionList.addAll(departmentRepository.getDtoPositionList())
            _isChanged.value = false
        }
    }


    fun toNameList(posList: MutableListLiveData<Position>): List<String> {
        val list = mutableListOf<String>()
        list.add("최상위 직분")
        val currentList = posList.toList().map { it.name }
        list.addAll(currentList)
        return list
    }

    fun positionParentSpinnerSelectedChanged(position: Int) {
        if (position == 0) {
            detailPosition.value?.cat = 0
            return
        }
        detailPosition.value?.cat = editablePositionList.toList()[position - 1].code
    }

    fun applyDetailEditAction() {
        when (_detailType.value) {
            PositionDetailType.ADD -> addPosition()
            PositionDetailType.EDIT -> editPosition()
            else -> return
        }
        selectedIndexParentPosition.value = 0
        _applyDetailEditEvent.value = Event(Unit)
    }

    fun changedPositionListApply() {
        viewModelScope.launch {
            val isSuccess = departmentRepository.uploadPositionList(editablePositionList.toList())
            if (isSuccess) _exitEvent.value = Event(Unit)
            else _applyFailEvent.value = Event(Unit)
        }
    }

    private fun openAddPositionDialog(parent: Int) {
        _detailType.value = PositionDetailType.ADD

        var maxCode = editablePositionList.value?.maxByOrNull { it.code }?.code ?: 0
        maxCode += 1

        detailPosition.value = Position(maxCode, "", parent)
        selectedIndexParentPosition.value = if (parent == 0) 0 else editablePositionList.toList().indexOfFirst { it.code == parent } + 1
        _detailPositionEvent.value = Event(Unit)
    }

    private fun openEditPositionDialog(code: Int) {
        _detailType.value = PositionDetailType.EDIT

        val position = editablePositionList.value?.find { it.code == code }
        detailPosition.value = position
        selectedIndexParentPosition.value = if (position?.cat == 0) 0 else editablePositionList.toList().indexOfFirst { it.code == position?.cat } + 1
        _detailPositionEvent.value = Event(Unit)
    }

    private fun addPosition() {
        editablePositionList.add(detailPosition.value!!)
        adapter.submitList(editablePositionList.toList())
        adapter.notifyDataSetChanged()
        checkChanged()
    }

    private fun editPosition() {
        val index = editablePositionList.value?.indexOfFirst { it.code == detailPosition.value!!.code } ?: -1
        if ( index < 0 ) return

        editablePositionList.set(index, detailPosition.value!!)
        adapter.submitList(editablePositionList.toList())
        adapter.notifyDataSetChanged()
        checkChanged()
    }

    private fun deletePosition() {
        val index = editablePositionList.value?.indexOfFirst { it.code == detailPosition.value!!.code } ?: -1
        if ( index < 0 ) return

        editablePositionList.remove(editablePositionList.value!![index])
        adapter.submitList(editablePositionList.toList())
        adapter.notifyDataSetChanged()
        checkChanged()
    }

    private fun checkAndDeletePosition() {
        viewModelScope.launch {
            val isUsing = departmentRepository.getCheckPositionIsUsing(detailPosition.value!!.code)
            if (isUsing) _isUsingPosition.value = Event(Unit)
            else {
                _deleteEvent.value = Event(
                    DialogInterface.OnClickListener { d, _ ->
                        deletePosition()
                        d.dismiss()
                        _backEvent.value = Event(Unit)
                    }
                )
            }
        }
    }

    private fun checkChanged() {
        _isChanged.value = !editablePositionList.isListEquals(originalPositionList)
    }


    init {
        loadPositionList()
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdminPositionViewModel(DepartmentRepository.getInstance(application)!!) as T
        }
    }

    enum class PositionDetailType { ADD, EDIT }
}