package com.example.androidintermedieatesubmission.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.liveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.androidintermedieatesubmission.data.database.StoryDao
import com.example.androidintermedieatesubmission.data.database.StoryDatabase
import com.example.androidintermedieatesubmission.data.database.StoryRemoteMediator
import com.example.androidintermedieatesubmission.data.response.StoryResponseItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test

class StoryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: StoryDatabase
    private lateinit var dao: StoryDao

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StoryDatabase::class.java
        ).build()
        dao = database.storyDao()
    }

    @After
    fun closeDb() = database.close()


    @OptIn(ExperimentalPagingApi::class)
    fun getPaging()  : LiveData<PagingData<StoryResponseItem>>  {
        val storyDatabase = database
        val apiService = FakeApi()


        val remoteMediator =  StoryRemoteMediator(storyDatabase as StoryDatabase, apiService)

        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = remoteMediator,
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }




    @Test
    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    fun checkAja() = runTest {
        val storyDatabase = database
        val apiService = FakeApi()

        val pagingState = PagingState<Int, StoryResponseItem>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )

        val remoteMediator = StoryRemoteMediator(storyDatabase, apiService)
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }
}