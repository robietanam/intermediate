package com.example.androidintermedieatesubmission.data.response

import com.google.gson.annotations.SerializedName

data class AuthResponse (
    @SerializedName("error") var error : Boolean? = null,
    @SerializedName("message") var message : String? = null,
    @SerializedName("loginResult") var loginResult : LoginResult? = LoginResult()
)

data class LoginResult (
    @SerializedName("userId") var userId : String? = null,
    @SerializedName("name") var name : String? = null,
    @SerializedName("token") var token : String? = null

)