package com.example.androidintermedieatesubmission.data.source

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.androidintermedieatesubmission.data.database.StoryDatabase
import com.example.androidintermedieatesubmission.data.response.StoryResponse
import com.example.androidintermedieatesubmission.data.response.StoryResponseItem

class StoryPagingSource(private val token: String, private val database: StoryDatabase) :  PagingSource<Int, StoryResponseItem>() {

     companion object {
        const val INITIAL_PAGE_INDEX = 1
        fun snapshot(items: StoryResponse): PagingData<StoryResponseItem> {
            return PagingData.from(items.listStory)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StoryResponseItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryResponseItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX

            //val responseData = ApiConfig.getApiService(token).getStoriesPaging(location = 1, page = position, size = params.loadSize).listStory
            val responseData = database.storyDao().getAllStory()

            return responseData.load(params)
            /*LoadResult.Page(
                data = responseData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.isNullOrEmpty()) null else position + 1
            )*/
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}