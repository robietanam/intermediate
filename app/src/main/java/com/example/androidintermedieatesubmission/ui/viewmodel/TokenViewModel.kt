package com.example.androidintermedieatesubmission.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bangkit.navigationsubmission.data.preferences.AuthPreferences
import com.example.androidintermedieatesubmission.helper.AuthData
import kotlinx.coroutines.launch

class AuthViewModel(private val pref: AuthPreferences) : ViewModel() {

    fun getToken(): LiveData<AuthData> {
        return pref.getCredential().asLiveData()
    }

    fun saveToken(token: String, name: String, userId: String) {
        viewModelScope.launch {
            pref.saveCredential(AuthData(token = token, nama = name, userId = userId))
        }
    }


    fun resetToken() {
        viewModelScope.launch {
            pref.saveCredential(AuthData())
        }
    }
}

