package com.sjk.yoram.viewmodel

import android.app.Application
import android.content.DialogInterface
import android.util.Log
import androidx.lifecycle.*
import com.sjk.yoram.R
import com.sjk.yoram.model.Event
import com.sjk.yoram.model.UserPermission
import com.sjk.yoram.model.dto.GiveType
import com.sjk.yoram.model.dto.WorshipType
import com.sjk.yoram.model.ui.adapter.AdminGiveTypeListAdapter
import com.sjk.yoram.model.ui.adapter.AdminWorshipListAdapter
import com.sjk.yoram.model.ui.listener.AdminGiveTypeClickListener
import com.sjk.yoram.model.ui.listener.AdminWorshipClickListener
import com.sjk.yoram.repository.ServerRepository
import com.sjk.yoram.repository.UserRepository
import kotlinx.coroutines.launch

class PrefViewModel(private val userRepository: UserRepository, private val serverRepository: ServerRepository): ViewModel() {
    private val _userPermission = MutableLiveData<UserPermission>()
    val userPermission: LiveData<UserPermission>
        get() = _userPermission

    private val _toastEvent = MutableLiveData<Event<String>>()
    val toastEvent: LiveData<Event<String>>
        get() = _toastEvent

    private val _naviEvent = MutableLiveData<Event<Int>>()
    val naviEvent: LiveData<Event<Int>>
        get() = _naviEvent

    private val _backEvent = MutableLiveData<Event<Unit>>()
    val backEvent: LiveData<Event<Unit>>
        get() = _backEvent

    private val _applyEvent = MutableLiveData<Event<Unit>>()
    val applyEvent: LiveData<Event<Unit>>
        get() = _applyEvent

    private val _applyCancelEvent = MutableLiveData<Event<Unit>>()
    val applyCancelEvent: LiveData<Event<Unit>>
        get() = _applyCancelEvent

    private val _ruleType = MutableLiveData<RuleType>()
    val ruleType: LiveData<RuleType>
        get() = _ruleType

    private val _logoutEvent = MutableLiveData<Event<Unit>>()
    val logoutEvent: LiveData<Event<Unit>>
        get() = _logoutEvent

    // MARK: - AdminWorship
    private val worshipClickListener = object: AdminWorshipClickListener {
        override fun onClick(index: Int) {
            detailWorship.value = worshipList.value?.get(index)
            _detailWorshipEvent.value = Event(Unit)
        }
    }
    val worshipAdapter = AdminWorshipListAdapter(worshipClickListener)
    val worshipList = MutableLiveData<MutableList<WorshipType>>()

    val detailWorship = MutableLiveData<WorshipType>()

    private val _detailWorshipEvent = MutableLiveData<Event<Unit>>()
    val detailWorshipEvent: LiveData<Event<Unit>>
        get() = _detailWorshipEvent

    private val _deleteWorshipEvent = MutableLiveData<Event<DialogInterface.OnClickListener>>()
    val deleteWorshipEvent: LiveData<Event<DialogInterface.OnClickListener>>
        get() = _deleteWorshipEvent

    private val _usingWorshipEvent = MutableLiveData<Event<Unit>>()
    val usingWorshipEvent: LiveData<Event<Unit>>
        get() = _usingWorshipEvent

    private val _exitWorshipEvent = MutableLiveData<Event<Unit>>()
    val exitWorshipEvent: LiveData<Event<Unit>>
        get() = _exitWorshipEvent

    private val _exitGiveTypeEvent = MutableLiveData<Event<Unit>>()
    val exitGiveTypeEvent: LiveData<Event<Unit>>
        get() = _exitGiveTypeEvent


    // MARK: - AdminGive
    private val giveTypeClickListener = object: AdminGiveTypeClickListener {
        override fun onClick(index: Int) {
            detailGiveType.value = giveTypeList.value?.get(index)
            _detailGiveTypeEvent.value = Event(Unit)
        }
    }
    val giveTypeAdapter = AdminGiveTypeListAdapter(giveTypeClickListener)
    val giveTypeList = MutableLiveData<MutableList<GiveType>>()

    val detailGiveType = MutableLiveData<GiveType>()

    private val _detailGiveTypeEvent = MutableLiveData<Event<Unit>>()
    val detailGiveTypeEvent: LiveData<Event<Unit>>
        get() = _detailGiveTypeEvent

    private val _deleteGiveTypeEvent = MutableLiveData<Event<DialogInterface.OnClickListener>>()
    val deleteGiveTypeEvent: LiveData<Event<DialogInterface.OnClickListener>>
        get() = _deleteGiveTypeEvent

    private val _usingGiveTypeEvent = MutableLiveData<Event<Unit>>()
    val usingGiveTypeEvent: LiveData<Event<Unit>>
        get() = _usingGiveTypeEvent

    init {
        viewModelScope.launch {
            _userPermission.value = UserPermission.values()[userRepository.getMyPermission(userRepository.getLoginID())]
        }
        worshipList.value = mutableListOf()
        giveTypeList.value = mutableListOf()
    }

    fun btnEvent(btnId: Int) {
        when (btnId) {
            R.id.frag_my_pref_noti_push -> { showToast("기능 준비중입니다.") }
            R.id.frag_my_pref_rule_rule -> { moveFragment(R.id.action_prefFragment_to_prefRuleFragment); _ruleType.value = RuleType.APP }
            R.id.frag_my_pref_rule_privacy -> { moveFragment(R.id.action_prefFragment_to_prefRuleFragment); _ruleType.value = RuleType.PRIVACY }
            R.id.frag_my_pref_account_privacy -> { moveFragment(R.id.action_prefFragment_to_prefPrivacyFragment) }
            R.id.frag_my_pref_account_logout -> { moveFragment(R.id.action_prefFragment_to_prefLogoutDialogFragment) }
            R.id.frag_my_pref_admin_banner -> { moveFragment(R.id.action_prefFragment_to_adminBannerFragment) }
            R.id.frag_my_pref_admin_worship -> initAdminWorship()
            R.id.frag_my_pref_admin_worship_header_add -> addWorshipType()
            R.id.frag_my_pref_admin_worship_body_apply -> moveFragment(R.id.action_adminWorshipFragment_to_prefApplyDialogFragment)
            R.id.dialog_my_admin_worship_edit_delete -> checkAndDeleteWorshipType()
            R.id.dialog_my_admin_worship_edit_cancel -> _backEvent.value = Event(Unit)
            R.id.dialog_my_admin_worship_edit_apply -> applyWorshipTypeEdited()
            R.id.frag_my_pref_admin_give -> initGiveType()
            R.id.frag_my_pref_admin_give_header_add -> addGiveTypeAction()
            R.id.frag_my_pref_admin_give_body_apply -> moveFragment(R.id.action_adminGiveTypeFragment_to_prefApplyDialogFragment)
            R.id.dialog_my_admin_give_edit_delete -> checkAndDeleteGiveType()
            R.id.dialog_my_admin_give_edit_apply -> applyGiveTypeEdited()
            R.id.dialog_my_pref_logout_ok -> { _logoutEvent.value = Event(Unit) }
            R.id.dialog_my_pref_apply_cancel -> { _applyCancelEvent.value = Event(Unit) }
        }
    }
    fun backBtn() {
        _backEvent.value = Event(Unit)
    }

    fun changedValueApply() {
        _applyEvent.value = Event(Unit)
    }


    private fun showToast(message: String) {
        _toastEvent.value = Event(message)
    }

    private fun moveFragment(actionId: Int) {
        _naviEvent.value = Event(actionId)
    }

    private fun initAdminWorship() {
        viewModelScope.launch {
            worshipList.value?.clear()
            worshipList.value = serverRepository.getWorshipList().toMutableList()
            worshipAdapter.notifyDataSetChanged()
            moveFragment(R.id.action_prefFragment_to_adminWorshipFragment)
        }
    }

    private fun addWorshipType() {
        detailWorship.value = WorshipType(-1, "")
        _detailWorshipEvent.value = Event(Unit)
    }

    private fun applyWorshipTypeEdited() {
        if (detailWorship.value?.id == -1) {
            detailWorship.value?.id = worshipList.value?.size!!
            addWorship(detailWorship.value!!)
        } else {
            for (i in 0 until worshipList.value!!.size) {
                if (detailWorship.value?.id == worshipList.value?.get(i)?.id) {
                    setWorship(i, detailWorship.value!!)
                }
            }
        }
        _backEvent.value = Event(Unit)
    }

    private fun checkAndDeleteWorshipType() {
        viewModelScope.launch {
            val isUsing = serverRepository.getCheckUsingWorshipType(detailWorship.value?.id!!)
            if (isUsing) {
                _usingWorshipEvent.value = Event(Unit)
            } else {
                _deleteWorshipEvent.value = Event(
                    DialogInterface.OnClickListener { p0, _ ->
                        val index = worshipList.value?.indexOfFirst { it.id == detailWorship.value?.id!! }
                        removeWorship(index!!)
                        p0.dismiss()
                        _backEvent.value = Event(Unit)
                    }
                )
            }
        }
    }

    private fun setWorship(index: Int, worship: WorshipType) {
        if (worshipList.value == null) return
        val current = worshipList.value!!
        current[index] = worship
        worshipList.value = current
        worshipAdapter.notifyDataSetChanged()
    }

    private fun addWorship(worship: WorshipType) {
        if (worshipList.value == null) return
        val current = worshipList.value!!
        current.add(worship)
        worshipList.value = current
        worshipAdapter.notifyDataSetChanged()
    }

    private fun removeWorship(index: Int) {
        if (worshipList.value == null) return
        val current = worshipList.value!!
        current.removeAt(index)
        worshipList.value = current
        worshipAdapter.notifyDataSetChanged()
    }

    fun changedWorshipTypeApply() {
        viewModelScope.launch {
            for (i in 0 until worshipList.value!!.size) {
                worshipList.value!![i].id = i

                val result = serverRepository.editWorshipTypeList(worshipList.value!!)

                if (!result) {
                    _toastEvent.value = Event("적용에 실패했습니다. 다시 시도해 주세요")
                } else {
                    _exitWorshipEvent.value = Event(Unit)
                }
            }
        }
    }

    private fun initGiveType() {
        viewModelScope.launch {
            giveTypeList.value?.clear()
            giveTypeList.value = serverRepository.getAllGiveTypeList().toMutableList()
            giveTypeAdapter.notifyDataSetChanged()
            moveFragment(R.id.action_prefFragment_to_adminGiveTypeFragment)
        }
    }

    private fun addGiveTypeAction() {
        detailGiveType.value = GiveType("", -1)
        _detailGiveTypeEvent.value = Event(Unit)
    }

    private fun applyGiveTypeEdited() {
        if (detailGiveType.value?.type == -1) {
            detailGiveType.value?.type = giveTypeList.value?.size ?: 0
            addGiveType(detailGiveType.value!!)
        } else {
            for (i in 0 until giveTypeList.value!!.size) {
                if (detailGiveType.value?.type == giveTypeList.value?.get(i)?.type) {
                    setGiveType(i, detailGiveType.value!!)
                }
            }
        }
        _backEvent.value = Event(Unit)
    }

    private fun checkAndDeleteGiveType() {
        viewModelScope.launch {
            val isUsing = serverRepository.getCheckUsingGiveType(detailGiveType.value?.type!!)
            if (isUsing) {
                _usingGiveTypeEvent.value = Event(Unit)
            } else {
                _deleteGiveTypeEvent.value = Event(
                    DialogInterface.OnClickListener { p0, _ ->
                        val index = giveTypeList.value?.indexOfFirst { it.type == detailGiveType.value?.type!! }
                        removeGiveType(index!!)
                        p0.dismiss()
                        _backEvent.value = Event(Unit)
                    }
                )
            }
        }
    }

    fun changedGiveTypeApply() {
        viewModelScope.launch {
            for (i in 0 until giveTypeList.value!!.size) {
                giveTypeList.value!![i].type = i

                val result = serverRepository.editGiveTypeList(giveTypeList.value!!)

                if (!result) {
                    _toastEvent.value = Event("적용에 실패했습니다. 다시 시도해 주세요")
                } else {
                    _exitGiveTypeEvent.value = Event(Unit)
                }
            }
        }
    }

    private fun setGiveType(index: Int, giveType: GiveType) {
        if (giveTypeList.value == null) return
        val current = giveTypeList.value!!
        current[index] = giveType
        giveTypeList.value = current
        giveTypeAdapter.notifyDataSetChanged()
    }

    private fun addGiveType(giveType: GiveType) {
        if (giveTypeList.value == null) return
        val current = giveTypeList.value!!
        current.add(giveType)
        giveTypeList.value = current
        giveTypeAdapter.notifyDataSetChanged()
    }

    private fun removeGiveType(index: Int) {
        if (giveTypeList.value == null) return
        val current = giveTypeList.value!!
        current.removeAt(index)
        giveTypeList.value = current
        giveTypeAdapter.notifyDataSetChanged()
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PrefViewModel(UserRepository.getInstance(application)!!, ServerRepository.getInstance(application)!!) as T
        }
    }

    enum class RuleType { APP, PRIVACY }
}