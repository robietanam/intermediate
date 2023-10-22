package com.example.androidintermedieatesubmission.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.navigationsubmission.data.retrofit.ApiConfig
import com.example.androidintermedieatesubmission.data.response.AuthResponse
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {

    companion object{
        private const val TAG = "REGISTER_VIEW_MODEL"

        data class RegisterForm(val name: String, val email: String, val password: String)
    }

    private val _auth = MutableLiveData<AuthResponse>()
    val auth: LiveData<AuthResponse> = _auth

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun register(registerForm: RegisterForm){
        _isLoading.value = true
        val client = ApiConfig.getApiService().register(email = registerForm.email, password = registerForm.password, name = registerForm.name)
        client.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: retrofit2.Call<AuthResponse>, response: Response<AuthResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _auth.value = response.body()
                } else {
                    _auth.value = AuthResponse(error = true, message = response.message())
                    Log.e(RegisterViewModel.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<AuthResponse>, t: Throwable) {
                _isLoading.value = false
                if (t.message.toString().startsWith("failed to connect")){
                    _auth.value = AuthResponse(error = true, message = "Failed to connect API")
                }
                Log.e(RegisterViewModel.TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

}