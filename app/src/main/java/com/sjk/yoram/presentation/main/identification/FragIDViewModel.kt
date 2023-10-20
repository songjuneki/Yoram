package com.sjk.yoram.presentation.main.identification

import android.app.Application
import android.graphics.Bitmap
import android.widget.RadioGroup
import androidx.lifecycle.*
import com.budiyev.android.codescanner.DecodeCallback
import com.sjk.yoram.R
import com.sjk.yoram.data.entity.WorshipType
import com.sjk.yoram.util.Event
import com.sjk.yoram.util.MutableListLiveData
import com.sjk.yoram.presentation.common.listener.RadioItemSelectedListener
import com.sjk.yoram.data.repository.ServerRepository
import com.sjk.yoram.data.repository.UserRepository
import com.sjk.yoram.util.MySecurity
import com.sjk.yoram.util.hexToByteArray
import kotlinx.coroutines.*

class FragIDViewModel(private val userRepository: UserRepository, private val serverRepository: ServerRepository): ViewModel() {
    private val _isValidCode = MutableLiveData<Boolean>()
    val isValidCode: LiveData<Boolean>
        get() = _isValidCode

    private val _timer = MutableLiveData<Int>()
    val timer: LiveData<Int>
        get() = _timer

    private val _code = MutableLiveData<Bitmap>()
    val code: LiveData<Bitmap>
        get() = _code

    private val _navEvent = MutableLiveData<Event<Int>>()
    val navEvent: LiveData<Event<Int>>
        get() = _navEvent

    private val _openScannerEvent = MutableLiveData<Event<Int>>()
    val openScannerEvent: LiveData<Event<Int>>
        get() = _openScannerEvent

    private val _backEvent = MutableLiveData<Event<Unit>>()
    val backEvent: LiveData<Event<Unit>>
        get() = _backEvent

    private val worshipList = MutableListLiveData<WorshipType>()
    val worshipStringList: LiveData<List<String>>
        get() = worshipList.map { list ->
            list.map { it.name }
        }

    val selectedWorship = MutableLiveData<WorshipType>()


    private val _frontCam = MutableLiveData<Event<Boolean>>()
    val frontCam: LiveData<Event<Boolean>>
        get() = _frontCam

    private val _scanEnabled = MutableLiveData<Event<Boolean>>()
    val scanEnabled: LiveData<Event<Boolean>>
        get() = _scanEnabled

    private val _scanResult = MutableLiveData<Event<Boolean>>()
    val scanResult: LiveData<Event<Boolean>>
        get() = _scanResult

    private val _showToast = MutableLiveData<Event<String>>()
    val showToast: LiveData<Event<String>>
        get() = _showToast

    private var _hiddenCount = 0


    init {
        _hiddenCount = 0
        viewModelScope.launch {
            _timer.value = 0
            countStop()
            _frontCam.value = Event(false)
            _scanEnabled.value = Event(true)
        }
    }

    fun btnEvent(id: Int) {
        when (id) {
            R.id.frag_id_refresh -> { makeCode(); countDown() }
            R.id.frag_id_scanner -> {
                loadWorshipList()
                _navEvent.value = Event(R.id.action_iDFragment_to_scannerInitDialog)
            }
            R.id.dialog_scanner_done -> { _openScannerEvent.value = Event(R.id.action_scannerInitDialog_to_scannerDialog) }
            R.id.dialog_scanner_cancel -> { _backEvent.value = Event(Unit) }
            R.id.dialog_scanner_close -> { _backEvent.value = Event(Unit) }
            R.id.dialog_scanner_cam -> { _frontCam.value = Event(!_frontCam.value!!.peekContent())
            }
        }
    }


    private fun makeCode() {
        viewModelScope.launch {
            _code.value = userRepository.getUserCode(_hiddenCount > 15)
            _isValidCode.value = true
        }
    }

    private fun makeNotValidCode() {
        _code.value = userRepository.getNotValidUserCode()
    }

    var countDownJob: Job? = null

    fun countDown() {
        countDownJob = viewModelScope.launch {
            _timer.value = 16
            while (isActive) {
                if (_timer.value!! == 0)  {
                    countStop()
                    return@launch
                }
                _timer.value = _timer.value!!.minus(1)
                delay(1000L)
            }
        }
    }

    fun countStop() {
        countDownJob?.cancel()
        _isValidCode.value = false
        _timer.value = 0
        makeNotValidCode()
    }


    private fun loadWorshipList() {
        viewModelScope.launch {
            worshipList.clear()
            worshipList.addAll(serverRepository.getWorshipList())
            selectedWorship.value = null
        }
    }

    val worshipSelectChange = object: RadioItemSelectedListener {
        override fun onChange(view: RadioGroup, item: String) {
            val selected = worshipList.value!!.find { (it.name == item) }
            if (selected != null) {
                selectedWorship.value = selected
            }
        }
    }

    val decoder = DecodeCallback {
        codeChecker(it.text)
    }

    private var codeCheckerJob: Job? = null
    private fun codeChecker(code: String) {
        codeCheckerJob = viewModelScope.async {
            val dec = MySecurity().decodeBase64(code).hexToByteArray().decodeToString()

            if (dec == "####") {
                codeFailure("QR SCAN :: not private qr!")
                return@async
            }
            if (dec.substring(0, 14) != "GUSEONGCHURCH;") {
                codeFailure("QR SCAN :: not guseong church!")
                return@async
            }
            if (dec.substring(14, 28) != "BY.SONGJUNEKI;") {
                codeFailure("QR SCAN :: wrong signature!")
                return@async
            }
            val checkerid = userRepository.getLoginID()
            val wtype = selectedWorship.value!!.id
            val info = dec.substring(28).split(";")

            if (info[0].startsWith("ERROR:")) {
                codeFailure("QR SCAN :: qr is something wrong! [SYSTEM]")
                return@async
            }
            if (!info[0].startsWith("DATE:")) {
                codeFailure("QR SCAN :: qr is something wrong! [DATE]")
                return@async
            }
            if (!info[1].startsWith("TIME:")) {
                codeFailure("QR SCAN :: qr is something wrong! [TIME]")
                return@async
            }
            if (!info[2].startsWith("ID:")) {
                codeFailure("QR SCAN :: qr is something wrong! [ID]")
                return@async
            }
            val attend = com.sjk.yoram.data.entity.Attend(
                info[2].substring(3).toInt(),
                info[0].substring(5),
                info[1].substring(5),
                wtype,
                checkerid
            )

            codeSuccess(attend)
        }
    }

    private fun codeFailure(error: String) {
        _scanEnabled.value = Event(false)
        _scanResult.value = Event(false)
        _showToast.value = Event(error)
    }

    private suspend fun codeSuccess(attend: com.sjk.yoram.data.entity.Attend) {
        if (userRepository.attendUser(attend)) {
            _scanEnabled.value = Event(false)
            _scanResult.value = Event(true)
        } else {
            _scanEnabled.value = Event(true)
        }
    }

    fun notifyEnded() {
        _scanEnabled.value = Event(true)
    }

    fun hiddenFeature() {
        _hiddenCount++
    }


    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FragIDViewModel(UserRepository.getInstance(application)!!, ServerRepository.getInstance(application)!!) as T
        }
    }
}