package com.example.androidintermedieatesubmission

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bangkit.navigationsubmission.data.preferences.AuthPreferences
import com.bangkit.navigationsubmission.data.preferences.dataStore
import com.bangkit.navigationsubmission.ui.viewmodel.ViewModelMainFactory
import com.example.androidintermedieatesubmission.databinding.ActivityLoginBinding
import com.example.androidintermedieatesubmission.helper.requestPermissionLauncher
import com.example.androidintermedieatesubmission.ui.components.ButtonWithLoadingCustom
import com.example.androidintermedieatesubmission.ui.viewmodel.AuthViewModel
import com.example.androidintermedieatesubmission.ui.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var myButton: ButtonWithLoadingCustom
    private lateinit var loginViewModel: LoginViewModel


    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.INTERNET
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        requestPermissionLauncher(this, REQUIRED_PERMISSION)

        val pref = AuthPreferences.getInstance(application.dataStore)

        myButton = binding.btnSubmit
        val registerText = binding.btnRegister

        var tokenViewModel = ViewModelProvider(this, ViewModelMainFactory(pref))[AuthViewModel::class.java]

        loginViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[LoginViewModel::class.java]

        checkIsNotEmpty()

        loginViewModel.auth.observe(this){
            Log.d("APAAJA", it.toString())
            if (it.error == true){
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()

            } else if (it.error == false){
                tokenViewModel.saveToken(token = it.loginResult!!.token!!, name = it.loginResult!!.name!!, userId = it.loginResult!!.userId!!)
                tokenViewModel.getToken().observe(this){
                    if (it.token != "" && it.token?.isNotEmpty() == true){
                        val intentDetail = Intent(this, ListStoriesActivity::class.java)
                        intentDetail.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intentDetail.putExtra(ListStoriesActivity.TOKEN_INTENT_KEY, it)
                        startActivity(intentDetail)
                    }
                }
                checkIsNotEmpty()

            } else {
                Toast.makeText(this, "Unknown Error", Toast.LENGTH_SHORT).show()
            }

            if (!binding.editTextCustom.text.isNullOrEmpty() && !binding.passwordEditTextCustom.text.isNullOrEmpty()){
                setMyButtonEnable(true)
            }
        }

        loginViewModel.isLoading.observe(this){
            setMyButtonLoading(it)
        }



        registerText.setOnClickListener {
            val intentDetail = Intent(this, RegisterActivity::class.java)
            startActivity(intentDetail)
        }

        binding.editTextCustom.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkIsNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })


        binding.passwordEditTextCustom.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkIsNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        myButton.setOnClickListener {
            if(checkIsNotEmpty()){
                loginViewModel.login(LoginViewModel.Companion.LoginForm(email = binding.editTextCustom.text.toString(), password = binding.passwordEditTextCustom.text.toString()))
            } else {
                Toast.makeText(this, "Masukkan data dengan benar", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun checkIsNotEmpty() : Boolean {

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+.[A-Za-z0-9.-]\$"

        if (!binding.editTextCustom.text.isNullOrEmpty() && !binding.passwordEditTextCustom.text.isNullOrEmpty() ) {
            if (binding.passwordEditTextCustom.text!!.length >= 8 && binding.editTextCustom.text!!.matches(emailRegex.toRegex())){
                setMyButtonEnable(true)
                return true
            } else {

                setMyButtonEnable(false)
            }
        } else {
            setMyButtonEnable(false)
        }

        return false
    }

    private fun setMyButtonLoading(value: Boolean){
        setMyButtonEnable(false)
        myButton.setLoading(value)
    }

    private fun setMyButtonEnable(value: Boolean) {
        myButton.isEnabled = value
    }
}