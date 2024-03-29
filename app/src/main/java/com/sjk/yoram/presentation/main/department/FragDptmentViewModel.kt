package com.sjk.yoram.presentation.main.department

import android.annotation.SuppressLint
import android.app.Application
import android.widget.DatePicker.OnDateChangedListener
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.*
import com.google.android.material.textfield.TextInputLayout
import com.sjk.yoram.R
import com.sjk.yoram.data.entity.*
import com.sjk.yoram.data.repository.DepartmentRepository
import com.sjk.yoram.data.repository.ServerRepository
import com.sjk.yoram.data.repository.UserRepository
import com.sjk.yoram.presentation.common.listener.TextInputChanged
import com.sjk.yoram.presentation.common.listener.UserItemClickListener
import com.sjk.yoram.presentation.common.model.UserPermission
import com.sjk.yoram.presentation.main.department.*
import com.sjk.yoram.presentation.main.department.manager.ManagerGiveListAdapter
import com.sjk.yoram.presentation.main.department.manager.UserManagerDepartmentChangedListener
import com.sjk.yoram.presentation.main.department.manager.UserManagerDepartmentListAdapter
import com.sjk.yoram.util.Event
import com.sjk.yoram.util.MutableListLiveData
import com.sjk.yoram.util.MutableUserLiveData
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener
import kotlinx.coroutines.*
import java.math.BigInteger
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

class FragDptmentViewModel(private val userRepository: UserRepository, private val serverRepository: ServerRepository, private val departmentRepository: DepartmentRepository): ViewModel() {
    private var _myPermission: Int = -5

    private val _dptSortType = MutableLiveData(DptButtonType.NAME)
    val dptSortType: LiveData<DptButtonType> = _dptSortType

    val dptSortIdx = MutableLiveData(0)

    var departmentNodeList = MutableListLiveData<DepartmentNode>()

    private val _isLoadingDptServer = MutableLiveData(Event(true))
    val isLoadingDptServer: LiveData<Event<Boolean>>
        get() = _isLoadingDptServer

    private val _searchResult = MutableLiveData<List<SimpleUser>>()
    val searchResult: LiveData<List<SimpleUser>>
        get() = _searchResult

    val userDetail = MutableUserLiveData()

    private val _userDetailEvent = MutableLiveData<Event<Unit>>()
    val userDetailEvent: LiveData<Event<Unit>>
        get() = _userDetailEvent

    private val _userCallEvent = MutableLiveData<Event<String>>()
    val userCallEvent: LiveData<Event<String>>
        get() = _userCallEvent

    private val _userMsgEvent = MutableLiveData<Event<String>>()
    val userMsgEvent: LiveData<Event<String>>
        get() = _userMsgEvent

    private val _searchEvent = MutableLiveData<Event<Unit>>()
    val searchEvent: LiveData<Event<Unit>>
        get() = _searchEvent

    val isSearching = MutableLiveData(false)
    val searchKeyword = MutableLiveData("")

    // - User Management

    private val _userManageEvent = MutableLiveData<Event<Unit>>()
    val userManageEvent: LiveData<Event<Unit>>
        get() = _userManageEvent

    private val _userManagerCloseEvent = MutableLiveData<Event<Unit>>()
    val userManagerCloseEvent: LiveData<Event<Unit>>
        get() = _userManagerCloseEvent

    private val _userManagerFragEvent = MutableLiveData<Event<Int>>()
    val userManagerFragEvent: LiveData<Event<Int>>
        get() = _userManagerFragEvent

    private val _userManagerBackEvent = MutableLiveData<Event<Unit>>()
    val userManagerBackEvent: LiveData<Event<Unit>>
        get() = _userManagerBackEvent

    private val userManagerDptChanged = object: UserManagerDepartmentChangedListener {
        override var selectedDpt: Department = Department("성도", 0)
            get() = _checkedManagerDpt.value ?: Department("성도", 0)

        @SuppressLint("NotifyDataSetChanged")
        override fun onChanged(changedDpt: Department) {
            _checkedManagerDpt.value = changedDpt
            userManagerDepartmentAdapter.notifyDataSetChanged()
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onExpanded() {
            userManagerDepartmentAdapter.notifyDataSetChanged()
        }
    }

    private val userManagerPosChanged = object: UserManagerDepartmentChangedListener {
        override var selectedDpt: Department = Department("성도", 1050)
            get() = Department(_checkedManagerPos.value ?: Position(1050, "성도", 1050))

        @SuppressLint("NotifyDataSetChanged")
        override fun onChanged(changedDpt: Department) {
            _checkedManagerPos.value = Position(changedDpt.code, changedDpt.name, changedDpt.parentCode)
            userManagerPositionAdapter.notifyDataSetChanged()
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onExpanded() {
            userManagerPositionAdapter.notifyDataSetChanged()
        }
    }

    val userManagerDepartmentAdapter: UserManagerDepartmentListAdapter = UserManagerDepartmentListAdapter(userManagerDptChanged)
    val userManagerPositionAdapter: UserManagerDepartmentListAdapter = UserManagerDepartmentListAdapter(userManagerPosChanged)

    val departmentList = MutableLiveData<MutableList<Department>>()
    val positionList = MutableLiveData<MutableList<Department>>()

    private val _checkedManagerDpt = MutableLiveData<Department>()
    val checkedManagerDpt: LiveData<Department>
        get() = _checkedManagerDpt

    private val _checkedManagerPos = MutableLiveData<Position>()
    val checkedManagerPos: LiveData<Position>
        get() = _checkedManagerPos

    private val _checkedManagerPerm = MutableLiveData<UserPermission>()
    val checkedManagerPerm: LiveData<UserPermission>
        get() = _checkedManagerPerm

    private val _yearSpinner = MutableLiveData(emptyList<String>())
    val yearSpinner: MutableLiveData<List<String>> = _yearSpinner

    private val _monthSpinner = MutableLiveData(emptyList<Int>())
    val monthSpinner: MutableLiveData<List<Int>> = _monthSpinner

    private var _giveDateList: HashMap<String, List<Int>> = hashMapOf()

    val selectedIndexGiveYear = MutableLiveData(0)

    val selectedIndexGiveMonth = MutableLiveData(0)

    val userGiveList = MutableLiveData<MutableList<Give>>()

    val selectedGive = MutableLiveData<Give>()

    val editableGiveAmount = MutableLiveData<String>()

    private val _detailGiveEvent = MutableLiveData<Event<Unit>>()
    val detailGiveEvent: LiveData<Event<Unit>>
        get() = _detailGiveEvent

    private val _giveTypeList: MutableList<GiveType> = mutableListOf()
    private val _giveTypeSpinner = MutableLiveData(emptyList<String>())
    val giveTypeSpinner: MutableLiveData<List<String>> = _giveTypeSpinner
    val selectedGiveType = MutableLiveData<Int>()

    private val _giveWorshipTypeList: MutableList<WorshipType> = mutableListOf()
    private val _giveWorshipTypeSpinner = MutableLiveData(emptyList<String>())
    val giveWorshipTypeSpinner: MutableLiveData<List<String>> = _giveWorshipTypeSpinner
    val  selectedGiveWorshipType = MutableLiveData<Int>()

    init {
        viewModelScope.launch {
            _myPermission = userRepository.getMyPermission(userRepository.getLoginID())
            departmentNodeList.value = mutableListOf()
            _searchResult.value = listOf()
            loadDepartmentByName()
            departmentList.value = mutableListOf()
            positionList.value = mutableListOf()
        }
    }

    val sortTypeListener = OnSpinnerItemSelectedListener<String> { _, _, newIndex, _ ->
        changeDptSortType(newIndex)
    }

    fun clickEvent(btnId: Int) {
        when(btnId) {
            R.id.frag_dptment_header_search -> { if (_myPermission > 0) isSearching.value = true; _searchEvent.value = Event(Unit) }
            R.id.frag_dptment_header_cancel -> hideSearchbar()
            R.id.dialog_user_info_manage -> initUserManager()
        }
    }

    fun managerClickEvent(btnId: Int) {
        when(btnId) {
            R.id.frag_user_manager_home_apply -> commitUserManagerEdited()
            R.id.frag_user_manager_home_cancel -> _userManagerCloseEvent.value = Event(Unit)
            R.id.frag_user_manager_home_dpt_btn -> initUserManagerDepartment()
            R.id.frag_user_manager_dptment_back -> _userManagerBackEvent.value = Event(Unit)
            R.id.frag_user_manager_home_pos_btn -> initUserManagerPosition()
            R.id.frag_user_manager_pos_back -> _userManagerBackEvent.value = Event(Unit)
            R.id.frag_user_manager_home_perm_btn -> _userManagerFragEvent.value = Event(R.id.action_userManagerHome_to_userManagerPermission)
            R.id.frag_user_manager_perm_back -> _userManagerBackEvent.value = Event(Unit)
            R.id.frag_user_manager_home_give_btn -> initUserManagerGive()
            R.id.frag_user_manager_give_back -> _userManagerBackEvent.value = Event(Unit)
            R.id.frag_user_manager_give_add -> initUserManagerGiveAdd()
            R.id.frag_user_manager_give_detail_back, R.id.frag_user_manager_give_detail_cancel -> _userManagerBackEvent.value = Event(Unit)
            R.id.frag_user_manager_give_detail_apply -> commitUserManagerGiveEdited()
            R.id.frag_user_manager_give_detail_zero -> editableGiveAmount.value = "0"
            R.id.frag_user_manager_give_detail_delete -> deleteUserManagerGive()
        }
    }

    fun hideSearchbar() {
        this.isSearching.value = false
        this.searchKeyword.value = ""
    }


    val searchInputChanged = object: TextInputChanged {
        override fun afterTextChanged(view: TextInputLayout, input: String) {
            _searchJob?.cancel()
            _searchJob = null
            if (input.isBlank()) {
                _searchResult.value = listOf()
                return
            }

            if (input.isDigitsOnly()) {
                viewModelScope.launch {
                    searchUserByNumber(input)
                }
            } else {
                viewModelScope.launch {
                    searchUserByName(input)
                }
            }
        }
    }

    private val userClickListener = object: UserItemClickListener {
        override fun onClick(user: SimpleUser) {
            selectedUser(user.id)
        }
    }
    var nodeListAdapter: ExpandableDepartmentNodeListAdapter = ExpandableDepartmentNodeListAdapter(departmentNodeList.value ?: mutableListOf(), userClickListener)
    fun searchListAdapter() : SimpleUserListAdapter = SimpleUserListAdapter(userClickListener)

    private fun selectedUser(id: Int) {
        viewModelScope.launch {
            userDetail.value = userRepository.getUserDetail(id)
            _userDetailEvent.value = Event(Unit)
        }
    }

    private var _searchJob: Job? = null
    private fun searchUserByName(keyword: String) {
        _searchJob = viewModelScope.launch {
            _searchResult.value = departmentRepository.searchUserByName(keyword, userRepository.getRequestUser())
        }
    }

    private fun searchUserByNumber(keyword: String) {
        _searchJob = viewModelScope.launch {
            _searchResult.value = departmentRepository.searchUserByNum(keyword, userRepository.getRequestUser())
        }
    }

    private fun loadDepartmentByName() = viewModelScope.launch {
        departmentNodeList.clear()
        _isLoadingDptServer.value = Event(true)
        departmentNodeList.value = departmentRepository.getDepartmentNodeListByName(userRepository.getRequestUser())
        _isLoadingDptServer.value = Event(false)
    }

    private fun loadDepartmentByDepartment() = viewModelScope.launch {
        departmentNodeList.clear()
        _isLoadingDptServer.value = Event(true)
        departmentNodeList.value = departmentRepository.getDepartmentNodeListByDepartment(userRepository.getRequestUser())
        _isLoadingDptServer.value = Event(false)
    }

    private fun loadDepartmentByPosition() = viewModelScope.launch {
        departmentNodeList.clear()
        _isLoadingDptServer.value = Event(true)
        departmentNodeList.value = departmentRepository.getDepartmentNodeListByPosition(userRepository.getRequestUser())
        _isLoadingDptServer.value = Event(false)
    }

    private fun loadDepartmentList() {
        viewModelScope.launch {
            val selectedDpt = _checkedManagerDpt.value!!
            departmentList.value = departmentRepository.getDepartmentList().onEach {
                if (selectedDpt.parentCode == it.code) it.isExpanded = true
            }
        }
    }

    private fun loadPositionList() {
        viewModelScope.launch {
            val selectedPos = _checkedManagerPos.value!!
            positionList.value = departmentRepository.getPositionList().onEach {
                if (selectedPos.cat == it.code) it.isExpanded = true
            }
        }
    }

    private fun changeDptSortType(index: Int) {
        when (index) {
            0 -> {
                _dptSortType.value = DptButtonType.NAME
                dptSortIdx.value = 0
                loadDepartmentByName()
            }
            1 -> {
                _dptSortType.value = DptButtonType.DEPARTMENT
                dptSortIdx.value = 1
                loadDepartmentByDepartment()
            }
            2 -> {
                _dptSortType.value = DptButtonType.POSITION
                dptSortIdx.value = 2
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

    fun getPermissionName(permission: UserPermission): String {
        return when(permission) {
            UserPermission.NONE -> "0. 권한 없음"
            UserPermission.NORMAL -> "1. 일반 성도"
            UserPermission.FINANCE -> "2. 재정 권한"
            UserPermission.PERSONNEL -> "3. 인사 권한"
            UserPermission.FINANCE_PERSONNEL -> "4. 재정과 인사 권한"
            UserPermission.ADMIN -> "5. 관리 권한"
            UserPermission.SUPER_ADMIN -> "6. 개발자"
        }
    }
    fun getPermissionName(permission: Int): String {
        return when(permission) {
            0 -> "0. 권한 없음"
            1 -> "1. 일반 성도"
            2 -> "2. 재정 권한"
            3 -> "3. 인사 권한"
            4 -> "4. 재정과 인사 권한"
            5 -> "5. 관리 권한"
            6 -> "6. 개발자"
            else -> "오류"
        }
    }

    private fun initUserManager() {
        viewModelScope.launch {
            _checkedManagerDpt.value = if (userDetail.getDepartment() == 0) Department("성도", 0) else Department(userDetail.getDepartment())
            _checkedManagerPos.value = departmentRepository.getPositionByCode(userDetail.getPosition())
            _checkedManagerPerm.value = userDetail.getPermission()
            _userManageEvent.value = Event(Unit)
        }
    }

    private fun initUserManagerDepartment() {
        _userManagerFragEvent.value = Event(R.id.action_userManagerHome_to_userManagerDptment)
        loadDepartmentList()
    }

    private fun initUserManagerPosition() {
        _userManagerFragEvent.value = Event(R.id.action_userManagerHome_to_userManagerPosition)
        loadPositionList()
    }

    private fun initUserManagerGive() {
        viewModelScope.launch {
            _giveDateList = userRepository.getDateListHasGive(userDetail.getUserID())
            selectedIndexGiveYear.value = 0
            _yearSpinner.value = _giveDateList.keys.toList()
            _userManagerFragEvent.value = Event(R.id.action_userManagerHome_to_userManagerGive)
        }
    }

    private fun initUserManagerGiveDetail() {
        viewModelScope.launch {
            _giveTypeList.clear()
            _giveTypeList.addAll(serverRepository.getAllGiveTypeList())
            _giveTypeSpinner.value = _giveTypeList.map { it.name }
            selectedGiveType.value = _giveTypeList.indexOfFirst { it.type == selectedGive.value?.give_type }

            _giveWorshipTypeList.clear()
            _giveWorshipTypeList.addAll(serverRepository.getWorshipList())
            _giveWorshipTypeSpinner.value = _giveWorshipTypeList.map { it.name }
            selectedGiveWorshipType.value = _giveWorshipTypeList.indexOfFirst { it.id == selectedGive.value?.worship_type }


            val formatting = DecimalFormat("###,###")
            editableGiveAmount.value = formatting.format(selectedGive.value?.amount)
            _detailGiveEvent.value = Event(Unit)
        }
    }

    private fun initUserManagerGiveAdd() {
        viewModelScope.launch {
            _giveTypeList.clear()
            _giveTypeList.addAll(serverRepository.getAllGiveTypeList())
            _giveTypeSpinner.value = _giveTypeList.map { it.name }
            selectedGiveType.value = 0

            _giveWorshipTypeList.clear()
            _giveWorshipTypeList.addAll(serverRepository.getWorshipList())
            _giveWorshipTypeSpinner.value = _giveWorshipTypeList.map { it.name }
            selectedGiveWorshipType.value = 0

            val now = LocalDate.now()
            val date = "${now.year}-${String.format("%02d", now.monthValue)}-${String.format("%02d", now.dayOfMonth)}"
            selectedGive.value = Give(-1, userDetail.getUserID(), 0, "", 0, date, BigInteger.ZERO)
            editableGiveAmount.value = "0"
            _detailGiveEvent.value = Event(Unit)
        }
    }

    fun yearSpinnerSelectedChanged(position: Int) {
        val year = _giveDateList.keys.toList()[position]
        val months = _giveDateList[year]?.sortedDescending()
        _monthSpinner.value = months
        selectedIndexGiveMonth.value = 0
    }

    fun monthSpinnerSelectedChanged(position: Int) {
        viewModelScope.async {
            val year = _giveDateList.keys.toList()[selectedIndexGiveYear.value ?: 0]
            val month = _giveDateList[year]!!.sortedDescending()[position]
            userGiveList.value = userRepository.getRawUserGiveList(userDetail.getUserID(), LocalDate.of(year.toInt(), month, 1))
        }
    }

    fun giveTypeSpinnerSelectedChanged(position: Int) {
        selectedGive.value?.give_type = _giveTypeList[position].type
    }

    fun giveWorshipTypeSpinnerSelectedChanged(position: Int) {
        selectedGive.value?.worship_type = _giveWorshipTypeList[position].id
    }

    private val _giveListItemClickListener = object: ManagerGiveListAdapter.GiveItemClickListener {
        override fun onClick(give: Give) {
            selectedGive.value = give
            initUserManagerGiveDetail()
        }
    }

    val giveListAdapter = ManagerGiveListAdapter(_giveListItemClickListener)

    fun setUserManagerPermission(permission: Int) {
        _checkedManagerPerm.value = UserPermission.values()[permission]
    }

    fun getLocalDateFromGive(give: Give): LocalDate =
        LocalDate.parse(give.date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    val dateChangedListener = OnDateChangedListener { _, year, month, day ->
        selectedGive.value?.date = "$year-${String.format("%02d", month+1)}-${String.format("%02d", day)}"
    }

    val giveAmountChanged = object: TextInputChanged {
        override fun afterTextChanged(view: TextInputLayout, input: String) {
            if (input.isEmpty() || input == "0")
                selectedGive.value?.amount = BigInteger.ZERO
            else
                selectedGive.value?.amount = input.replace(",", "").toBigInteger()
        }
    }

    fun easyGiveAmountButton(value: Int) {
        var amount = editableGiveAmount.value!!.replace(",", "").toBigInteger()
        amount = if (value > 0)
            amount.add(value.toBigInteger())
        else
            amount.subtract(value.absoluteValue.toBigInteger())

        val format = DecimalFormat("###,###")
        editableGiveAmount.value = format.format(amount)
    }

    private fun deleteUserManagerGive() {
        viewModelScope.launch {
            if ((selectedGive.value?.id ?: -1) < 0) {
                _userManagerBackEvent.value = Event(Unit)
                return@launch
            }

            if (userRepository.deleteGive(selectedGive.value!!))
                _userManagerBackEvent.value = Event(Unit)
        }
    }

    private fun commitUserManagerGiveEdited() {
        viewModelScope.launch {
            val result = if (selectedGive.value!!.id > 0)
                userRepository.editGive(selectedGive.value!!)
            else
                userRepository.insertNewGive(selectedGive.value!!)

            if (result) _userManagerBackEvent.value = Event(Unit)
        }
    }


    private fun commitUserManagerEdited() {
        viewModelScope.launch {
            userDetail.apply {
                val changedDpt = _checkedManagerDpt.value!!
                modifyDepartment(changedDpt.code, changedDpt.name)
                val changedPos = _checkedManagerPos.value!!
                modifyPosition(changedPos.code, changedPos.name)
                modifyPermission(_checkedManagerPerm.value!!)
            }

            val result = userRepository.editUserInfo(userDetail.value!!)

            if (result) {
                _userManagerCloseEvent.value = Event(Unit)
                when (_dptSortType.value!!) {
                    DptButtonType.NAME -> loadDepartmentByName()
                    DptButtonType.DEPARTMENT -> loadDepartmentByDepartment()
                    DptButtonType.POSITION -> loadDepartmentByPosition()
                }
            }
        }
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FragDptmentViewModel(UserRepository.getInstance(application)!!, ServerRepository.getInstance(application)!!, DepartmentRepository.getInstance(application)!!) as T
        }
    }
}