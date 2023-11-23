package com.example.androidintermedieatesubmission.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.bangkit.navigationsubmission.data.retrofit.ApiService
import com.example.androidintermedieatesubmission.data.database.StoryDatabase
import com.example.androidintermedieatesubmission.data.database.StoryRemoteMediator
import com.example.androidintermedieatesubmission.data.response.StoryResponseItem

class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: ApiService) {

    @OptIn(ExperimentalPagingApi::class)
    fun getPaging() : LiveData<PagingData<StoryResponseItem>> {

        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                // StoryPagingSource(token = token, database = storyDatabase)
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }
}