package com.example.androidintermedieatesubmission.di
import android.content.Context
import com.bangkit.navigationsubmission.data.retrofit.ApiConfig
import com.example.androidintermedieatesubmission.data.database.StoryDatabase
import com.example.androidintermedieatesubmission.data.repository.StoryRepository

object Injection {
    fun provideRepository(context: Context, token: String? = null): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService(token)
        return StoryRepository(database, apiService)
    }
}