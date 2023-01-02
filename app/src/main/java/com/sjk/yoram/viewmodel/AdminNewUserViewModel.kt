package com.sjk.yoram.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sjk.yoram.model.AdminInfo
import com.sjk.yoram.model.Event
import com.sjk.yoram.model.MutableListLiveData
import com.sjk.yoram.model.dto.NewUserForAdmin
import com.sjk.yoram.model.ui.adapter.AdminNewUserListAdapter
import com.sjk.yoram.model.ui.listener.AdminNewUserAcceptListener
import com.sjk.yoram.repository.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AdminNewUserViewModel(private val userRepository: UserRepository): ViewModel() {
    private var myInfo: AdminInfo = AdminInfo(-1, 0)

    val userList = MutableListLiveData<NewUserForAdmin>()

    private val acceptListener = object: AdminNewUserAcceptListener {
        override fun onAccept(id: Int, position: Int) {
            acceptNewUser(id, position)
        }
    }

    val adapter = AdminNewUserListAdapter(acceptListener)

    private val _toastEvent = MutableLiveData<Event<String>>()
    val toastEvent: LiveData<Event<String>>
            get() = _toastEvent

    private val _isRefreshing = MutableLiveData<Boolean>(false)
    val isRefreshing: LiveData<Boolean>
        get() = _isRefreshing


    fun refreshList() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            _isRefreshing.value = true
            userList.clear()
            userList.addAll(userRepository.getNewUserList(myInfo))
            adapter.notifyDataSetChanged()
            _isRefreshing.value = false
        }
    }

    private var refreshJob: Job? = null

    private fun makeToastMessage(msg: String) {
        _toastEvent.value = Event(msg)
    }

    private fun acceptNewUser(id: Int, position: Int) {
        viewModelScope.launch {
            val detail = userRepository.getUserDetail(id)
            detail.permission++
            if(userRepository.editUserInfo(detail))
                removeUser(id, position)
            else
                makeToastMessage("오류가 발생했습니다. 다시 시도해 주세요")
        }
    }

    private fun removeUser(id: Int, position: Int) {
        val user = userList.toList().find { it.id == id }
        userList.remove(user!!)
        adapter.notifyItemRemoved(position)
    }

    init {
        viewModelScope.launch {
            myInfo = AdminInfo(userRepository.getLoginID(), userRepository.getMyPermission(userRepository.getLoginID()))
            refreshList()
        }
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdminNewUserViewModel(UserRepository.getInstance(application)!!) as T
        }
    }
}