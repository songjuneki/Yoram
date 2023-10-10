package com.sjk.yoram.viewmodel

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.*
import androidx.paging.*
import androidx.savedstate.SavedStateRegistryOwner
import com.sjk.yoram.R
import com.sjk.yoram.model.dto.Board
import com.sjk.yoram.model.dto.ReservedBoardCategory
import com.sjk.yoram.repository.BoardRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
class FragBoardViewModel(private val savedStateHandle: SavedStateHandle, private val boardRepository: BoardRepository): ViewModel() {

    private val _uiState = MutableStateFlow(BoardFragmentUiState())
    val uiState: StateFlow<BoardFragmentUiState>
        get() = _uiState.asStateFlow()

    val boardPagingData: Flow<PagingData<Board>>

    private val changedBoardPagingData = MutableSharedFlow<ReservedBoardCategory>()

    override fun onCleared() {
        savedStateHandle[LAST_CATEGORY_LIST] = uiState.value.boardCatList
        savedStateHandle[LAST_SELECTED_CATEGORY] = uiState.value.currentCategory
        super.onCleared()
    }

    init {
        val lastCategoryList: List<ReservedBoardCategory> =
            savedStateHandle[LAST_CATEGORY_LIST] ?: emptyList()
        val lastCategory: ReservedBoardCategory? = savedStateHandle[LAST_SELECTED_CATEGORY]

        val changeBoardPagingData = changedBoardPagingData
            .distinctUntilChanged()
            .onStart {
                val list = boardRepository.getReservedCategoryList()
                emit(lastCategory
                    ?: lastCategoryList.firstOrNull()
                    ?: list.firstOrNull()
                    ?: return@onStart
                )
            }
            .onEach {
                _uiState.update { current ->
                    current.copy(isBoardInit = false)
                }
            }
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                replay = 1
            )

        if (lastCategoryList.isNotEmpty()) {
            _uiState.update { current ->
                current.copy(
                    isCatInit = true,
                    boardCatList = lastCategoryList
                )
            }
        } else {
            loadReservedCategoryList(lastCategory)
        }

        boardPagingData = changeBoardPagingData
            .flatMapLatest { boardRepository.getBoardPagingDataFlow(it) }
            .onCompletion {
                _uiState.update { current -> current.copy(isBoardInit = true) }
            }.cachedIn(viewModelScope)
    }

    private fun loadReservedCategoryList(lastCategory: ReservedBoardCategory? = null) = viewModelScope.launch {
        val list = boardRepository.getReservedCategoryList()
        _uiState.update { current ->
            current.copy(
                isCatInit = true,
                boardCatList = list,
                currentCategory = lastCategory
            )
        }
    }

    fun changeCurrentCategory(category: ReservedBoardCategory?)  {
        val changedCategory = category ?: uiState.value.boardCatList.firstOrNull()
        _uiState.update { current ->
            current.copy(
                isBoardInit = false,
                currentCategory = changedCategory
            )
        }

        viewModelScope.launch {
            changedCategory?.let {
                changedBoardPagingData.emit(it)
                if (it == uiState.value.currentCategory) {
                    _uiState.update { current ->
                        current.copy(isBoardInit = true)
                    }
                }
            }
        }
    }


    fun refreshBoardData() = viewModelScope.launch {
        val lastCurrentCategory = uiState.value.currentCategory
        _uiState.update { current ->
            current.copy(
                isCatInit = false,
                isBoardInit = false,
                currentCategory = null
            )
        }
        loadReservedCategoryList(lastCurrentCategory)
    }

    fun moveBoardDetail(board: Board?) {
        _uiState.update { current ->
            current.copy(
                isBoardDetail = board != null,
                boardDetail = board
            )
        }
    }

    class Factory(
        private val application: Application,
        val owner: SavedStateRegistryOwner,
        bundle: Bundle? = null
    ) : AbstractSavedStateViewModelFactory(owner, bundle) {
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return FragBoardViewModel(handle, BoardRepository.getInstance(application)!!) as T
        }

    }
}



data class BoardFragmentUiState(
    val isCatInit: Boolean = false,
    val isBoardInit: Boolean = false,
    val isBoardDetail: Boolean = false,
    val boardCatList: List<ReservedBoardCategory> = emptyList(),
    val currentCategory: ReservedBoardCategory? = null,
    val boardDetail: Board? = null,
    val error: ErrorType = ErrorType.STABLE,
)

enum class ErrorType {
    STABLE, NO_INTERNET, UNKNOWN
}

private const val LAST_CATEGORY_LIST = "last_category_list"
private const val LAST_SELECTED_CATEGORY = "last_selected_category"