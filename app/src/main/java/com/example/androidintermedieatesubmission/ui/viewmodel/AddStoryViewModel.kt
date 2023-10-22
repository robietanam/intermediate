package com.example.androidintermedieatesubmission.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.navigationsubmission.data.retrofit.ApiConfig
import com.example.androidintermedieatesubmission.data.response.StoryResponse
import com.example.androidintermedieatesubmission.helper.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryViewModel : ViewModel() {

    private val _story = MutableLiveData<StoryResponse>()
    val story: LiveData<StoryResponse> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    companion object {
        private const val TAG = "ADD_STORY_VIEW_MODEL"
    }

    fun uploadImage(imageFile: File, description: String, lat: Double?, lon: Double?, token: String) {
        _isLoading.value = true

        var latBody: RequestBody? = null
        var lonBody: RequestBody? = null
        val descriptionBody = description.toRequestBody("text/plain".toMediaType())
        if (lat != null && lon != null){

            latBody = lat.toString().toRequestBody("text/plain".toMediaType())
            lonBody = lon.toString().toRequestBody("text/plain".toMediaType())
        }
        val requestImageFile = imageFile.reduceFileImage().asRequestBody("image/jpeg".toMediaType())

        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )

        val client = ApiConfig.getApiService(token).addStories(multipartBody, description = descriptionBody, lat = latBody, lon = lonBody)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: retrofit2.Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                _isLoading.value = false

                Log.d(TAG, "APA HASIL: ${response.body()}")
                if (response.isSuccessful) {
                    _story.value = response.body()
                } else {
                    val failResp = response.body()
                    _story.value = StoryResponse(error = true, message = "Gagal upload")
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                _story.value = StoryResponse(error = true, message = t.message.toString())

                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

}