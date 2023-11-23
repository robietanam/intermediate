package com.example.androidintermedieatesubmission.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.androidintermedieatesubmission.DataDummy
import com.example.androidintermedieatesubmission.MainDispatcherRule
import com.example.androidintermedieatesubmission.data.repository.StoryRepository
import com.example.androidintermedieatesubmission.data.response.StoryResponseItem
import com.example.androidintermedieatesubmission.getOrAwaitValue
import com.example.androidintermedieatesubmission.ui.adapter.StoriesAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    private lateinit var storyViewModel: StoryViewModel

    @Test
    fun `Ketika Get Story dan data tidak null and return data`() = runTest {

        val dummyQuote = DataDummy.generateDummyStoryResponse()
        val data: PagingData<StoryResponseItem> = StoryPagingSource.snapshot(dummyQuote.listStory)
        val expectedQuote = MutableLiveData<PagingData<StoryResponseItem>>()
        expectedQuote.value = data
        Mockito.`when`(storyRepository.getPaging()).thenReturn(expectedQuote)

        val mainViewModel = StoryViewModel(storyRepository)
        val actualQuote: PagingData<StoryResponseItem> = mainViewModel.getPaging.getOrAwaitValue()

        println(actualQuote)
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualQuote)

        assertNotNull(differ.snapshot()) // Data tidak null
        assertEquals(dummyQuote.listStory.size, differ.snapshot().size) // Jumlah data sesuai yang diharapkan
        assertEquals(dummyQuote.listStory[0], differ.snapshot()[0]) // Data pertama sesuai
    }

    val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }


    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {

        val data: PagingData<StoryResponseItem> = PagingData.from(emptyList())
        val expectedQuote = MutableLiveData<PagingData<StoryResponseItem>>()
        expectedQuote.value = data
        Mockito.`when`(storyRepository.getPaging()).thenReturn(expectedQuote)
        val mainViewModel = StoryViewModel(storyRepository)
        val actualQuote: PagingData<StoryResponseItem> = mainViewModel.getPaging.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualQuote)
        assertEquals(0, differ.snapshot().size) // Data yang dikembalikan nol
    }
}



class StoryPagingSource : PagingSource<Int, LiveData<List<StoryResponseItem>>>() {
    companion object {
        fun snapshot(items: List<StoryResponseItem>): PagingData<StoryResponseItem> {
            return PagingData.from(items)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryResponseItem>>>): Int {
        return 0
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryResponseItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}