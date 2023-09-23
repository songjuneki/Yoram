package com.sjk.yoram.viewmodel

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.*
import androidx.savedstate.SavedStateRegistryOwner
import com.sjk.yoram.R
import com.sjk.yoram.model.Event
import com.sjk.yoram.model.dto.Board
import com.sjk.yoram.model.dto.ReservedBoardCategory
import com.sjk.yoram.repository.BoardRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class FragBoardViewModel(private val savedStateHandle: SavedStateHandle, private val boardRepository: BoardRepository): ViewModel() {

    private val _uiState = MutableStateFlow(BoardFragmentUiState())
    val uiState: StateFlow<BoardFragmentUiState>
        get() = _uiState.asStateFlow()

    val boardPagingDataFlow: Flow<PagingData<Board>>

    val accept: (BoardFragmentUiAction) -> Unit

    fun btnEvent(id: Int) {
        when (id) {
            R.id.frag_board_detail_top_back -> accept(BoardFragmentUiAction.OnClickBoardDetail(null))
        }
    }

    override fun onCleared() {
        savedStateHandle[LAST_CATEGORY_LIST] = uiState.value.boardCatList
        savedStateHandle[LAST_SELECTED_CATEGORY] = uiState.value.currentCategory
        super.onCleared()
    }
    init {
        val lastCategoryList: List<ReservedBoardCategory> = savedStateHandle[LAST_CATEGORY_LIST] ?: emptyList()
        val lastCategory: ReservedBoardCategory? = savedStateHandle[LAST_SELECTED_CATEGORY]
        val actionStateFlow = MutableSharedFlow<BoardFragmentUiAction>()

        val loadCategory = actionStateFlow
            .filterIsInstance<BoardFragmentUiAction.LoadCategoryList>()
            .distinctUntilChanged()
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart {
                val list = lastCategoryList.ifEmpty { loadReservedCategoryList() }
                _uiState.update { current ->
                    current.copy(
                        isCatInit = true,
                        boardCatList = list,
                        isBoardInit = current.currentCategory != null
                                && current.currentCategory == lastCategory,
                        currentCategory = lastCategory
                    )
                }
                emit(BoardFragmentUiAction.LoadCategoryList(list))
            }
            .onEach {
                _uiState.update { current ->
                    current.copy(
                        isCatInit = true,
                        boardCatList = it.categoryList,
                        isBoardInit = current.currentCategory != null
                    )
                }
            }

        val categoryChanged = actionStateFlow
            .filterIsInstance<BoardFragmentUiAction.CategoryChange>()
            .distinctUntilChanged()
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart {
                val category = lastCategory
                    ?: lastCategoryList.firstOrNull()
                    ?: uiState.value.currentCategory
                    ?: uiState.value.boardCatList.firstOrNull()
                    ?: return@onStart
                emit(BoardFragmentUiAction.CategoryChange(category = category))
            }
            .onEach {
                _uiState.update { current ->
                    current.copy(
                        isBoardInit = false,
                        currentCategory = it.category
                    )
                }
            }

        val loadPagingData = actionStateFlow
            .filterIsInstance<BoardFragmentUiAction.CategoryChange>()
            .distinctUntilChanged()
            .onStart { BoardFragmentUiAction.CategoryChange(lastCategory ?: return@onStart) }
            .mapLatest { BoardFragmentUiAction.LoadBoardPagingData(loadBoardPagingData(it.category)) }
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onEach {
                _uiState.update { current ->
                    current.copy(
                        isBoardInit = true
                    )
                }
            }

        boardPagingDataFlow = loadPagingData
            .flatMapLatest { it.boardPagingDataFlow }
            .cachedIn(viewModelScope)

        val onClickedBoard = actionStateFlow
            .filterIsInstance<BoardFragmentUiAction.OnClickBoardDetail>()
            .distinctUntilChanged()
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 0
            )
            .onEach {
                Log.d("JKJK", "UiAction::OnClickBoardDetail::${it.board}")
                _uiState.update { current ->
                    current.copy(
                        isBoardDetail = it.board != null,
                        boardDetail = it.board
                    )
                }
            }

        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }

        combine(loadCategory,
            categoryChanged,
            ::Pair)
            .launchIn(viewModelScope)

        onClickedBoard.launchIn(viewModelScope)
    }

    private suspend fun loadReservedCategoryList(): List<ReservedBoardCategory> {
        _uiState.update { currentUiState ->
            currentUiState.copy(isCatInit = false)
        }
        return boardRepository.getReservedCategoryList()
    }
    private fun loadBoardPagingData(category: ReservedBoardCategory?): Flow<PagingData<Board>> {
        _uiState.update { currentUiState ->
            currentUiState.copy(isBoardInit = false)
        }
        return boardRepository.getBoardPagingDataFlow(category)
    }


    class Factory(private val application: Application, val owner: SavedStateRegistryOwner, bundle: Bundle? = null): AbstractSavedStateViewModelFactory(owner, bundle) {
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return FragBoardViewModel(handle, BoardRepository.getInstance(application)!!) as T
        }

    }
}

sealed class BoardFragmentUiAction {
    data class LoadCategoryList(val categoryList: List<ReservedBoardCategory> = emptyList()): BoardFragmentUiAction()
    data class CategoryChange(val category: ReservedBoardCategory): BoardFragmentUiAction()
    data class LoadBoardPagingData(val boardPagingDataFlow: Flow<PagingData<Board>>): BoardFragmentUiAction()
    data class OnClickBoardDetail(val board: Board?): BoardFragmentUiAction()
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