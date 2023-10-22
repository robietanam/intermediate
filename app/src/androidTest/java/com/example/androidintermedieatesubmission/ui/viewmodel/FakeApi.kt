package com.example.androidintermedieatesubmission.ui.viewmodel

import com.bangkit.navigationsubmission.data.retrofit.ApiService
import com.example.androidintermedieatesubmission.data.response.AuthResponse
import com.example.androidintermedieatesubmission.data.response.StoryResponse
import com.example.androidintermedieatesubmission.data.response.StoryResponseItem
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call

class FakeApi : ApiService {
    private val items = (0..100).map{
        StoryResponseItem(id = it.toString())
    }

    override fun login(email: String, password: String): Call<AuthResponse> {
        TODO("Not yet implemented")
    }

    override fun register(name: String, email: String, password: String): Call<AuthResponse> {
        TODO("Not yet implemented")
    }

    override fun getStories(page: Int?, size: Int?, location: Int?): Call<StoryResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getStoriesPaging(page: Int?, size: Int?, location: Int?): StoryResponse {
        val pageVal = page ?: 1
        val sizeVal = size ?: 10
        val newItem = ArrayList(items.slice((if (pageVal == 1) pageVal else pageVal * sizeVal)..sizeVal))
        return StoryResponse(error = false, message = "test", listStory = newItem)
    }

    override fun addStories(
        photo: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): Call<StoryResponse> {
        TODO("Not yet implemented")
    }

    override fun getStory(userId: String): Call<StoryResponse> {
        TODO("Not yet implemented")
    }
}