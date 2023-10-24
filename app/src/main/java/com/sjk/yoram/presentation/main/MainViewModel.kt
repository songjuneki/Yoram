package com.sjk.yoram.presentation.main

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.*
import com.sjk.yoram.R
import com.sjk.yoram.data.entity.Board
import com.sjk.yoram.data.entity.MyLoginData
import com.sjk.yoram.data.entity.ReservedBoardCategory
import com.sjk.yoram.presentation.common.model.LoginState
import com.sjk.yoram.presentation.common.model.UserPermission
import com.sjk.yoram.data.repository.ServerRepository
import com.sjk.yoram.data.repository.UserRepository
import com.sjk.yoram.util.Event
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainViewModel(private val userRepository: UserRepository, private val serverRepository: ServerRepository): ViewModel() {
    val dptClickState = MutableLiveData(false)
    val loginState = MutableLiveData(LoginState.NONE)

    private val _loginData = MutableLiveData<MyLoginData>()
    val loginData: LiveData<MyLoginData>
        get() = _loginData

    private val _avatar = MutableLiveData<Bitmap>()
    val avatar: LiveData<Bitmap>
        get() = _avatar

    private val _giveAmount = MutableLiveData<String>()
    val giveAmount: LiveData<String>
        get() = _giveAmount

    private val _currentFragment = MutableLiveData<FragmentType>(FragmentType.Fragment_HOME)
    val currentFragment: LiveData<FragmentType>
        get() = _currentFragment

    private val _loginEvent = MutableLiveData<Event<Unit>>()
    val loginEvent: LiveData<Event<Unit>>
        get() = _loginEvent

    private val _goDptSearchEvent = MutableLiveData<Event<Unit>>()
    val goDptSearchEvent: LiveData<Event<Unit>>
        get() = _goDptSearchEvent


    private val _boardCategoryEvent = MutableLiveData<Event<ReservedBoardCategory>>()
    val boardCategoryEvent: LiveData<Event<ReservedBoardCategory>>
        get() = _boardCategoryEvent

    private val _goBoardDetailEvent = MutableLiveData<Event<Board>>()
    val goBoardDetailEvent: LiveData<Event<Board>>
        get() = _goBoardDetailEvent


    private val _moveFragmentEvent = MutableLiveData<Event<Int>>()
    val moveFragmentEvent: LiveData<Event<Int>>
        get() = _moveFragmentEvent

    private val _privacyAgreeEvent = MutableLiveData<Event<Unit>>()
    val privacyAgreeEvent: LiveData<Event<Unit>>
        get() = _privacyAgreeEvent

    private val _showExpiredDialog = MutableLiveData(false)
    val showExpiredDialog: LiveData<Boolean>
        get() = _showExpiredDialog

    private val _onBottomMenuReSelected = MutableLiveData<Event<Unit>>()
    val onBottomMenuReselected: LiveData<Event<Unit>>
        get() = _onBottomMenuReSelected

    private val _backEvent = MutableLiveData<Event<Unit>>()
    val backEvent: LiveData<Event<Unit>>
        get() = _backEvent


    init {
        loadLoginData()
        loadGiveAmount()
        checkRuleAgreeExpire()
    }

    // Function :- for Fragment

    fun fragMoveEvent(btnId: Int) {
        when (btnId) {
            R.id.navi_home -> fragMoveHome()
            R.id.navi_dptment -> fragMoveDepartment()
            R.id.navi_id -> fragMoveID()
            R.id.navi_board -> fragMoveBoard()
            R.id.navi_my -> fragMoveMy()

            R.id.home_dpt_more, R.id.frag_my_user_menus_dpt -> fragMoveDepartment()
            R.id.home_dpt_search -> {
                fragMoveDepartment()
                _goDptSearchEvent.value = Event(Unit)
            }
            R.id.frag_my_user_menus_board -> fragMoveDepartment()
            R.id.home_checkin -> fragMoveID()
            R.id.main_dialog_update_layout -> _privacyAgreeEvent.value = Event(Unit)

            R.id.home_board_more -> fragMoveBoard()
        }
    }

    fun fragMoveHome() {
        _moveFragmentEvent.value = Event(R.id.navi_home)
    }

    fun fragMoveDepartment() {
        _moveFragmentEvent.value = Event(R.id.navi_dptment)
    }

    fun fragMoveID() {
        _moveFragmentEvent.value = Event(R.id.navi_id)
    }

    fun fragMoveBoard(board: Board? = null, category: ReservedBoardCategory? = null) {
        _moveFragmentEvent.value = Event(R.id.navi_board)

        board?.let {
            _goBoardDetailEvent.value = Event(it)
        }
        category?.let {
            _boardCategoryEvent.value = Event(it)
        }
    }

    fun fragMoveMy() {
        _moveFragmentEvent.value = Event(R.id.navi_my)
    }

    fun getCurrentFragment(): FragmentType {
        return _currentFragment.value ?: FragmentType.Fragment_HOME
    }

    fun setCurrentFragment(type: FragmentType) {
        this._currentFragment.value = type
    }

    fun loadLoginData() {
        viewModelScope.launch {
            val id = userRepository.getLoginID()
            if (id == -1) {
                _loginData.value = MyLoginData()
                loginState.value = LoginState.NONE
                _avatar.value = userRepository.getAvatarBitmap()
            } else {
                _loginData.value = userRepository.getLoginData(id)
                _avatar.value = userRepository.getAvatarBitmap()
                loginState.value = LoginState.LOGIN
            }
        }
    }

    fun loadGiveAmount() {
        viewModelScope.async {
            val id = userRepository.getLoginID()
            var total = BigInteger("0")
            if (id != -1)
                total = userRepository.getCurrentMonthGiveAmount(id)
            val moneyFormat = DecimalFormat("#,###")
            _giveAmount.value = moneyFormat.format(total)
        }
    }

    fun checkRuleAgreeExpire() {
        viewModelScope.async {
            val detail = userRepository.getUserDetail(userRepository.getLoginID())
            if (detail.privacy_agree_date.isNullOrEmpty()) return@async

            val privacyDateStr = detail.privacy_agree_date
            val privacyDate = LocalDateTime.parse(privacyDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            _showExpiredDialog.value = privacyDate.plusMonths(6).isBefore(LocalDateTime.now().minusDays(1))
        }
    }

    fun privacyRuleAgreeEvent() {
        viewModelScope.launch {
            val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            val detail = userRepository.getUserDetail(_loginData.value!!.id)
            detail.privacy_agree_date = now

            if (userRepository.editUserInfo(detail)) _backEvent.value = Event(Unit)
        }
    }

    fun login() {
        userRepository.setIsInit(true)
        _loginEvent.value = Event(Unit)
    }

    fun logout() {
        userRepository.setLogin(-1, "@")
        loadLoginData()
//        userRepository.setIsInit(true)
//        _loginEvent.value = Event(Unit)
    }

    fun reSelectMenuItem() {
        _onBottomMenuReSelected.value = Event(Unit)
    }

    fun getUserPermission(): UserPermission {
        val data = _loginData.value ?: MyLoginData()
        if (data.permission < 1)
            return UserPermission.NONE
        if (data.permission >= UserPermission.values().size)
            return UserPermission.SUPER_ADMIN
        return UserPermission.values()[data.permission]
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(UserRepository.getInstance(application)!!, ServerRepository.getInstance(application)!!) as T
        }
    }
}