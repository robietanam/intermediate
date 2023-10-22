package com.example.androidintermedieatesubmission.helper

import android.app.Activity
import android.content.Intent
import com.example.androidintermedieatesubmission.LoginActivity
import com.example.androidintermedieatesubmission.ui.viewmodel.AuthViewModel

class AuthHelper {
    companion object {
        fun logOut(context: Activity, tokenViewModel: AuthViewModel){
            tokenViewModel.resetToken()
            val intentDetail = Intent(context, LoginActivity::class.java)
            intentDetail.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intentDetail)
        }
    }
}