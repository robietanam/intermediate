package com.example.androidintermedieatesubmission

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.androidintermedieatesubmission.databinding.ActivityRegisterBinding
import com.example.androidintermedieatesubmission.ui.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkIsNotEmpty()

        registerViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[RegisterViewModel::class.java]

        registerViewModel.auth.observe(this) {
            if (it.error != true) {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Unknown Error", Toast.LENGTH_SHORT).show()
            }

            checkIsNotEmpty()

        }

        registerViewModel.isLoading.observe(this) {
            setMyButtonLoading(it)
        }

        binding.btnRegister.setOnClickListener {
            val intentDetail = Intent(this, LoginActivity::class.java)
            intentDetail.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intentDetail)
        }

        binding.edPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkIsNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })


        binding.edEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkIsNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        binding.edName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                checkIsNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })


        binding.btnSubmit.setOnClickListener {
            if(checkIsNotEmpty()){
                registerViewModel.register(
                    RegisterViewModel.Companion.RegisterForm(
                        email = binding.edEmail.text.toString(),
                        password = binding.edPassword.text.toString(),
                        name = binding.edName.text.toString()
                    )
                )
            }
        }
    }

    private fun checkIsNotEmpty() : Boolean{

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+.[A-Za-z0-9.-]\$"

        if (!binding.edEmail.text.isNullOrEmpty() && !binding.edPassword.text.isNullOrEmpty() && !binding.edName.text.isNullOrEmpty() ) {
            if (binding.edPassword.text!!.length >= 8 && binding.edEmail.text!!.matches(emailRegex.toRegex())){
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
        binding.btnSubmit.setLoading(value)
    }

    private fun setMyButtonEnable(value: Boolean) {
        binding.btnSubmit.isEnabled = value
    }


}