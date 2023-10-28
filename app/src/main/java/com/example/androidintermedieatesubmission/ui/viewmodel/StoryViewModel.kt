package com.example.androidintermedieatesubmission.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bangkit.navigationsubmission.data.retrofit.ApiConfig
import com.example.androidintermedieatesubmission.data.repository.StoryRepository
import com.example.androidintermedieatesubmission.data.response.StoryResponse
import com.example.androidintermedieatesubmission.data.response.StoryResponseItem
import com.example.androidintermedieatesubmission.di.Injection
import retrofit2.Callback
import retrofit2.Response

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _story = MutableLiveData<StoryResponse>()
    val story: LiveData<StoryResponse> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object{
        private const val TAG = "UserViewModel"
    }

    val getPaging : LiveData<PagingData<StoryResponseItem>> = storyRepository.getPaging().cachedIn(viewModelScope)


    fun getAll(token: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService(token).getStories(location = 1)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: retrofit2.Call<StoryResponse>, response: Response<StoryResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _story.value = response.body()
                } else {
                    _story.value = response.body()
                    if (response.code() == 401){
                        _story.value = StoryResponse(error = true, message = response.message())
                    }
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                _story.value = StoryResponse(error = true, message = t.message.toString())
                Log.e(TAG, "onFailure Fatal: ${t.message.toString()}")
            }
        })
    }
}


class ViewModelFactoryStory(private val context: Context, private val token: String? = null) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoryViewModel(Injection.provideRepository(context, token = token)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}