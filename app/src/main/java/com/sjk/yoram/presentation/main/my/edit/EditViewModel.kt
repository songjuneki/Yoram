package com.sjk.yoram.presentation.main.my.edit

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.*
import com.google.android.material.textfield.TextInputLayout
import com.sjk.yoram.R
import com.sjk.yoram.data.entity.Juso
import com.sjk.yoram.util.Event
import com.sjk.yoram.util.MutableUserLiveData
import com.sjk.yoram.presentation.common.listener.TextInputChanged
import com.sjk.yoram.data.repository.ServerRepository
import com.sjk.yoram.data.repository.UserRepository
import com.sjk.yoram.presentation.common.adapter.AddressListAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class EditViewModel(private val userRepository: UserRepository, private val serverRepository: ServerRepository): ViewModel() {
    val user = MutableUserLiveData()
    val avatar = MutableLiveData<Bitmap>()

    private val _exitEvent = MutableLiveData<Event<Unit>>()
    val exitEvent: LiveData<Event<Unit>>
        get() = _exitEvent

    private val _backEvent = MutableLiveData<Event<Unit>>()
    val backEvent: LiveData<Event<Unit>>
        get() = _backEvent

    private val _navEvent = MutableLiveData<Event<Int>>()
    val navEvent: LiveData<Event<Int>>
        get() = _navEvent

    private val _picDialogEvent = MutableLiveData<Event<Unit>>()
    val picDialogEvent: LiveData<Event<Unit>>
        get() = _picDialogEvent

    private val _galleryPickerEvent = MutableLiveData<Event<Unit>>()
    val galleryPickerEvent: LiveData<Event<Unit>>
        get() = _galleryPickerEvent

    private val _cameraPickerEvent = MutableLiveData<Event<Unit>>()
    val cameraPickerEvent: LiveData<Event<Unit>>
        get() = _cameraPickerEvent

    private val _deleteAvatarEvent = MutableLiveData<Event<Unit>>()
    val deleteAvatarEvent: LiveData<Event<Unit>>
        get() = _deleteAvatarEvent

    private val _loadingEvent = MutableLiveData<Event<Unit>>()
    val loadingEvent: LiveData<Event<Unit>>
        get() = _loadingEvent

    private val _addrKeyword = MutableLiveData<String>("")
    val addrKeyword: LiveData<String>
        get() = _addrKeyword

    private val _addrSearchResult = MutableLiveData<List<Juso>>()
    val addrSearchResult: LiveData<List<Juso>>
        get() = _addrSearchResult


    init {
        viewModelScope.async {
            user.value = userRepository.getUserDetail(userRepository.getLoginID())
            avatar.value = userRepository.getAvatarBitmap(userRepository.getLoginID())
        }
    }

    fun btnEvent(btnId: Int) {
        when(btnId) {
            R.id.my_edit_back -> { _exitEvent.value = Event(Unit) }
            R.id.my_edit_done -> { editDone() }
            R.id.my_edit_avatar -> { moveFragment(R.id.action_editFragment_to_editAvatarDialog) }

            R.id.dialog_avatar_gall_layout -> { _galleryPickerEvent.value = Event(Unit) }
            R.id.dialog_avatar_camera_layout -> { _cameraPickerEvent.value = Event(Unit) }
            R.id.dialog_avatar_del_layout -> { avatar.value = null; _deleteAvatarEvent.value = Event(Unit) }
            R.id.dialog_avatar_cancel -> { _backEvent.value = Event(Unit) }

            R.id.my_edit_bd -> { moveFragment(R.id.action_editFragment_to_editBDDialogFragment) }
            R.id.my_edit_address -> { moveFragment(R.id.action_editFragment_to_editAddressDialogFragment) }

            R.id.dialog_edit_bd_cancel, R.id.dialog_edit_address_close -> { _backEvent.value = Event(Unit) }
        }
    }

    private fun moveFragment(actionId: Int) {
        _navEvent.value = Event(actionId)
    }

    private fun editDone() {
        showLoading()
        viewModelScope.launch {
            userRepository.uploadAvatar(avatar.value)
            userRepository.editUserInfo(user.value!!)
            _exitEvent.value = Event(Unit)
        }
    }

    private fun showLoading() {
        _loadingEvent.value = Event(Unit)
    }

    fun setBD(str: String) {
        user.modifyBirth(str)
    }

    val phoneInputChanged = object: TextInputChanged {
        override fun afterTextChanged(view: TextInputLayout, input: String) {
            user.modifyPhone(input)
        }
    }

    val telInputChanged = object: TextInputChanged {
        override fun afterTextChanged(view: TextInputLayout, input: String) {
            user.modifyTel(input)
        }
    }

    val addrMoreInputChanged = object: TextInputChanged {
        override fun afterTextChanged(view: TextInputLayout, input: String) {
            user.modifyAddrm(input)
        }
    }

    val carInputChanged = object: TextInputChanged {
        override fun afterTextChanged(view: TextInputLayout, input: String) {
            user.modifyCar(input)
        }
    }


    val addrSearchInputChanged = object: TextInputChanged {
        override fun afterTextChanged(view: TextInputLayout, input: String) {
            _addrKeyword.value = input
            if (input.isEmpty()) _addrSearchResult.value = listOf()
            addrSearchJob?.cancel()
            searchAddr(input)
        }
    }

    private var addrSearchJob: Job? = null

    val addrItemClickListener = object: AddressListAdapter.AddressItemClickListener {
        override fun onClick(juso: Juso) {
            user.modifyAddr(juso.roadAddr)
            _addrKeyword.value = ""
            _addrSearchResult.value = listOf()
            _backEvent.value = Event(Unit)
        }
    }

    private fun searchAddr(keyword: String) {
        addrSearchJob = viewModelScope.launch {
            val result = serverRepository.searchAddress(keyword)
            _addrSearchResult.value = result
        }
    }


    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EditViewModel(UserRepository.getInstance(application)!!, ServerRepository.getInstance(application)!!) as T
        }
    }
}