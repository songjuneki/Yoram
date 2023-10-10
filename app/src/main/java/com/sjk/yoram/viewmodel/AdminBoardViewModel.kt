package com.sjk.yoram.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sjk.yoram.model.dto.BoardCategory
import com.sjk.yoram.model.dto.ReservedBoardCategory
import com.sjk.yoram.repository.BoardRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminBoardViewModel(private val boardRepository: BoardRepository): ViewModel() {
    private val _uiState = MutableStateFlow(AdminBoardUiState())
    val uiState: StateFlow<AdminBoardUiState>
        get() = _uiState.asStateFlow()

    init {
        initialListData()
    }

    fun initialListData() = viewModelScope.launch {
        _uiState.update {
            AdminBoardUiState()
        }
        val reservedList = loadCurrentReservedBoardCategoryList()
        val inReservedList = loadInReservedBoardCategoryList()

        _uiState.update { current ->
            current.copy(
                isLoaded = true,
                isChanged = false,
                currentList = reservedList,
                reserveList = reservedList,
                hideList = inReservedList
            )
        }
    }


    private suspend fun loadCurrentReservedBoardCategoryList(): List<ReservedBoardCategory> {
        return boardRepository.getReservedCategoryList()
    }

    private suspend fun loadInReservedBoardCategoryList(): List<BoardCategory> = boardRepository.getHidingCategoryList()

    private fun checkChanged(current: List<ReservedBoardCategory>, list: List<ReservedBoardCategory>): Boolean {
        if (current.size != list.size)
            return true

        val currentList = current.map { it.board_id }
        val compareList = list.map { it.board_id }

        repeat(current.size) {
            if (currentList[it] != compareList[it])
                return true
        }
        return false
    }

    fun onReorderInReserveList(list: List<ReservedBoardCategory>) {
        _uiState.update { current ->
            current.copy(
                isChanged = checkChanged(current.currentList, list),
                reserveList = list
            )
        }
    }

    fun onReorderInHideList(list: List<BoardCategory>) {
        _uiState.update { current ->
            current.copy(
                hideList = list
            )
        }
    }

    fun moveToHide(target: ReservedBoardCategory, lastPos: Int) {
        _uiState.update { current ->
            val reserveList = current.reserveList.toMutableList()
            if (reserveList.getOrNull(lastPos)?.id == target.id)
                reserveList.removeAt(lastPos)
            else
                reserveList.remove(target)

            val hideList = current.hideList.toMutableList()
            hideList.add(target.toBoardCategory())

            current.copy(
                isChanged = checkChanged(current.currentList, reserveList),
                reserveList = reserveList,
                hideList = hideList
            )
        }
    }

    fun moveToReserve(target: BoardCategory, lastPos: Int) {
        _uiState.update { current ->
            val reserveList = current.reserveList.toMutableList()
            reserveList.add(
                ReservedBoardCategory(
                    id = (reserveList.maxOfOrNull { it.id } ?: 0) + 1,
                    name = target.name,
                    board_id = target.id.toInt(),
                    board_type = target.type.name,
                    order = reserveList.size
                )
            )

            val hideList = current.hideList.toMutableList()
            if (hideList.getOrNull(lastPos)?.id == target.id)
                hideList.removeAt(lastPos)
            else
                hideList.remove(target)

            current.copy(
                isChanged = checkChanged(current.currentList, reserveList),
                reserveList = reserveList,
                hideList = hideList
            )
        }
    }

    fun applyReserveBoardCategoryList() = viewModelScope.launch {
        _uiState.update { current ->
            current.copy(isLoaded = false)
        }
        val uploadResult = boardRepository.uploadReserveCategoryList(_uiState.value.reserveList)

        if (uploadResult) {
            _uiState.update { current ->
                current.copy(
                    isLoaded = true,
                    isExit = true
                )
            }
        } else {
            _uiState.update { current ->
                current.copy(
                    isLoaded = true,
                    userMessage = "적용중 오류가 발생했습니다."
                )
            }
        }
    }

    fun messageShown() {
        _uiState.update { current ->
            current.copy(userMessage = null)
        }
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdminBoardViewModel(BoardRepository.getInstance(application)!!) as T
        }
    }
}

data class AdminBoardUiState(
    val isLoaded: Boolean = false,
    val isChanged: Boolean = false,
    val reserveList: List<ReservedBoardCategory> = emptyList(),
    val currentList: List<ReservedBoardCategory> = emptyList(),
    val hideList: List<BoardCategory> = emptyList(),
    val userMessage: String? = null,
    val isExit: Boolean = false
)