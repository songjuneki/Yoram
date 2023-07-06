package com.sjk.yoram.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sjk.yoram.model.ApiState
import com.sjk.yoram.model.MutableListLiveData
import com.sjk.yoram.model.dto.Board
import com.sjk.yoram.model.dto.BoardCategory
import com.sjk.yoram.model.dto.ReservedBoardCategory
import com.sjk.yoram.model.ui.adapter.BoardCategoryListAdapter
import com.sjk.yoram.model.ui.listener.BoardCategoryChangedListener
import com.sjk.yoram.repository.BoardRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FragBoardViewModel(private val boardRepository: BoardRepository): ViewModel() {

    // Property :- StateFlow

    private val _categoryList = MutableStateFlow<ApiState<List<ReservedBoardCategory>>>(ApiState.Loading())
    val categoryList: StateFlow<ApiState<List<ReservedBoardCategory>>> = _categoryList

    private val _postList = MutableStateFlow<ApiState<List<Board>>>(ApiState.Loading())
    val postList: StateFlow<ApiState<List<Board>>> = _postList

    private val _currentBoardCategory = MutableStateFlow<ReservedBoardCategory?>(null)
    val currentBoardCategory: StateFlow<ReservedBoardCategory?> = _currentBoardCategory

    private val _currentPage = MutableStateFlow<Int>(0)
    val currentPage: StateFlow<Int> = _currentPage


    // Property :- Adapter, Listener

    private val categoryClickListener = object: BoardCategoryChangedListener {
        override fun onChanged(changedCategory: ReservedBoardCategory) {
            if (currentBoardCategory.value?.id == changedCategory.id) return        // 같은 게시판 클릭시

            _currentBoardCategory.update { changedCategory }
            _currentPage.update { 0 }
//            loadBoardsPostList()
        }
    }

    fun categoryAdapter() = BoardCategoryListAdapter(categoryClickListener)

    init {
        viewModelScope.launch {
            boardRepository.getReservedCategoryList()
                .collect {
                    _categoryList.value = it
                }
        }
    }

    fun loadBoardsPostList() {
        viewModelScope.launch {
            if (_currentBoardCategory.value == null)
                return@launch
            boardRepository.getPagedBoardList(currentBoardCategory.value!!.toBoardCategory(), _currentPage.value)
                .collect {
                    _postList.getAndUpdate { current ->
                        val result = current.data?.toMutableList() ?: mutableListOf()
                        result.addAll(it.data ?: emptyList())
                        ApiState.Success(result)
                    }
                }
        }
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FragBoardViewModel(BoardRepository.getInstance(application)!!) as T
        }
    }
}