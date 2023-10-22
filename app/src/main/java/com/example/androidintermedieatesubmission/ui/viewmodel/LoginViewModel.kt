package com.example.androidintermedieatesubmission.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.navigationsubmission.data.retrofit.ApiConfig
import com.example.androidintermedieatesubmission.data.response.AuthResponse
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {

    companion object{
        private const val TAG = "LOGIN_VIEW_MODEL"

        data class LoginForm(val email: String, val password: String)
    }

    private val _auth = MutableLiveData<AuthResponse>()
    val auth: LiveData<AuthResponse> = _auth

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun login(loginForm: LoginForm){
        _isLoading.value = true
        val client = ApiConfig.getApiService().login(email = loginForm.email, password = loginForm.password)
        client.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: retrofit2.Call<AuthResponse>, response: Response<AuthResponse>) {
                _isLoading.value = false
                Log.e(TAG, "fucking call: ${response.isSuccessful} ${response.toString()}")
                if (response.isSuccessful) {
                    _auth.value = response.body()
                } else {
                    if (response.code() == 401){
                        _auth.value = AuthResponse(error = true, message = "Password salah")
                    } else if (response.code() == 400) {

                        _auth.value = AuthResponse(error = true, message = "Password atau email salah")
                    } else {
                        _auth.value = AuthResponse(error = true, message = response.message())
                    }
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<AuthResponse>, t: Throwable) {
                _isLoading.value = false
                if (t.message.toString().startsWith("failed to connect")){
                    _auth.value = AuthResponse(error = true, message = "Failed to connect API")
                }
                _auth.value = AuthResponse(error = true, message = t.message)
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

}