package com.sjk.yoram.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.sjk.yoram.model.*
import com.sjk.yoram.model.dto.Banner
import com.sjk.yoram.model.dto.Board
import com.sjk.yoram.model.dto.ReservedBoardCategory
import com.sjk.yoram.repository.BoardRepository
import com.sjk.yoram.repository.ServerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FragHomeViewModel(private val boardRepository: BoardRepository, private val serverRepository: ServerRepository): ViewModel() {
    private val _uiState = MutableStateFlow(FragHomeUiState())
    val uiState: StateFlow<FragHomeUiState>
        get() = _uiState.asStateFlow()


    init {
        loadNewHomeData()
    }

    fun loadNewHomeData() = viewModelScope.launch {
        val banners = loadBannerList()
        val categoryList = loadBoardCategoryList()
        val currentCategory = uiState.value.currentBoardCategory ?: categoryList.firstOrNull()
        val boardList = loadCurrentlyBoardList(currentCategory)

        _uiState.update {
            it.copy(
                bannerList = banners,
                boardCategoryList = categoryList,
                currentBoardCategory = currentCategory,
                boardList = boardList
            )
        }
    }

    fun categoryChanged(category: ReservedBoardCategory?) {
        val changed = category ?: uiState.value.boardCategoryList.firstOrNull()

        _uiState.update {
            it.copy(
                isBoardLoading = false,
                currentBoardCategory = changed
            )
        }

        viewModelScope.launch {
            changed?.let {
                _uiState.update {  current ->
                    current.copy(
                        isBoardLoading = true,
                        boardList = loadCurrentlyBoardList(it)
                    )
                }
            }
        }
    }


    private suspend fun loadBannerList(): List<Banner> {
        _uiState.update { it.copy(isBannerLoading = true) }
        val bannerList = serverRepository.getAllBanners().apply {
            _uiState.update { it.copy(isBannerLoading = false) }
        }

        return bannerList
    }
    private suspend fun loadBoardCategoryList(): List<ReservedBoardCategory> {
        _uiState.update { it.copy(isBoardCatLoading = true) }
        val list = boardRepository.getReservedCategoryList().apply {
            _uiState.update { it.copy(isBoardCatLoading = false) }
        }

        return list
    }

    private suspend fun loadCurrentlyBoardList(category: ReservedBoardCategory?): List<Board> {
        _uiState.update { it.copy(isBoardLoading = true) }
        if (category == null) {
            _uiState.update { it.copy(isBoardLoading = false) }
            return emptyList()
        }
        val board = boardRepository.getBoardList(category.toBoardCategory()).apply {
            _uiState.update { it.copy(isBoardLoading = false) }
        }
        val list = board?.take(BOARD_COUNT) ?: emptyList()


        return if (list.isNotEmpty()) {
            val addedList = list.toMutableList()
            addedList.add(list.last().copy(board_id = -15))
            addedList
        } else
            list

    }



    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FragHomeViewModel(BoardRepository.getInstance(application)!!, ServerRepository.getInstance(application)!!) as T
        }
    }

    companion object {
        private const val BOARD_COUNT = 5
    }
}



data class FragHomeUiState(
    val isBannerLoading: Boolean = true,
    val isBoardCatLoading: Boolean = true,
    val isBoardLoading: Boolean = true,
    val bannerList: List<Banner> = emptyList(),
    val boardCategoryList: List<ReservedBoardCategory> = emptyList(),
    val currentBoardCategory: ReservedBoardCategory? = null,
    val boardList: List<Board> = emptyList()
)