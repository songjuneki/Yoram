package com.sjk.yoram.model

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sjk.yoram.model.dto.Board
import com.sjk.yoram.model.dto.BoardCategory
import com.sjk.yoram.repository.BoardRepository
import retrofit2.HttpException
import java.io.IOException

class BoardPagingSource(private val boardRepository: BoardRepository, private val category: BoardCategory?): PagingSource<Int, Board>() {
    override fun getRefreshKey(state: PagingState<Int, Board>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Board> {
        val pageNumber = params.key ?: 0
        if (category == null)
            return LoadResult.Invalid()
        try {
            val result = boardRepository.getBoardList(category, pageNumber)
                ?: return LoadResult.Error(Throwable("Board response failure"))

            val prevKey = if (pageNumber > 0) pageNumber - 1 else null
            val nextKey = if (result.isNotEmpty()) pageNumber + 1 else null
            return LoadResult.Page(data = result,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }
}