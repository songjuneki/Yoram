package com.sjk.yoram.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.sjk.yoram.R
import com.sjk.yoram.model.ApiState
import com.sjk.yoram.model.BoardPagingSource
import com.sjk.yoram.model.Event
import com.sjk.yoram.model.dto.Board
import com.sjk.yoram.model.dto.ReservedBoardCategory
import com.sjk.yoram.model.ui.adapter.BoardCategoryListAdapter
import com.sjk.yoram.model.ui.adapter.BoardListAdapter
import com.sjk.yoram.model.ui.adapter.BoardListLoadStateAdapter
import com.sjk.yoram.model.ui.listener.BoardCategoryChangedListener
import com.sjk.yoram.model.ui.listener.BoardClickListener
import com.sjk.yoram.repository.BoardRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FragBoardViewModel(private val boardRepository: BoardRepository): ViewModel() {

    // Property :- for Category
    private val _categoryList = MutableStateFlow<ApiState<List<ReservedBoardCategory>>>(ApiState.Loading())
    val categoryList: StateFlow<ApiState<List<ReservedBoardCategory>>>
        get() = _categoryList

    val currentBoardCategory = MutableStateFlow<ReservedBoardCategory?>(null)

    // Property :- for Board
    val detailBoard = MutableLiveData<Board>()


    // Property :- Adapter, Listener
    private val categoryClickListener = object: BoardCategoryChangedListener {
        override fun onChanged(changedCategory: ReservedBoardCategory) {
            // 같은 게시판 클릭 시
            if (currentBoardCategory.value?.id == changedCategory.id) {
                _moveTopOfBoardListEvent.value = Event(Unit)
                return
            }
            currentBoardCategory.update { changedCategory }
        }
    }
    val categoryAdapter = BoardCategoryListAdapter(categoryClickListener)

    private val boardClickListener = object: BoardClickListener {
        override fun onClick(board: Board) {
            detailBoard.value = board
            _moveDetailEvent.value = Event(board)
        }
    }

    val boardAdapter = BoardListAdapter(boardClickListener).apply {
        this.withLoadStateFooter(BoardListLoadStateAdapter())
    }

    // Property :- for Event
    private val _moveTopOfBoardListEvent = MutableLiveData<Event<Unit>>()
    val moveTopOfBoardListEvent: LiveData<Event<Unit>>
        get() = _moveTopOfBoardListEvent

    private val _moveDetailEvent = MutableLiveData<Event<Board>>()
    val moveDetailEvent: LiveData<Event<Board>>
        get() = _moveDetailEvent

    private val _backEvent = MutableLiveData<Event<Unit>>()
    val backEvent: LiveData<Event<Unit>>
        get() = _backEvent

    fun btnEvent(id: Int) {
        when (id) {
            R.id.frag_board_detail_top_back -> _backEvent.value = Event(Unit)
        }
    }

    init {
        viewModelScope.launch {
            boardRepository.getReservedCategoryList()
                .collectLatest { data ->
                    _categoryList.update { data }
                    currentBoardCategory.update { data.data?.firstOrNull() }
                }
        }

    }

    fun getBoardData(category: ReservedBoardCategory): Flow<PagingData<Board>> {
        return Pager(config = PagingConfig(pageSize = 10, enablePlaceholders = true),
        pagingSourceFactory = { BoardPagingSource(boardRepository, category.toBoardCategory()) })
            .flow
            .cachedIn(viewModelScope)
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FragBoardViewModel(BoardRepository.getInstance(application)!!) as T
        }
    }
}