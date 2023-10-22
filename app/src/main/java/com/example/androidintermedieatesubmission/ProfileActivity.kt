package com.example.androidintermedieatesubmission

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bangkit.navigationsubmission.data.preferences.AuthPreferences
import com.bangkit.navigationsubmission.data.preferences.dataStore
import com.bangkit.navigationsubmission.ui.viewmodel.ViewModelMainFactory
import com.example.androidintermedieatesubmission.databinding.ActivityProfileBinding
import com.example.androidintermedieatesubmission.helper.AuthData
import com.example.androidintermedieatesubmission.helper.AuthHelper
import com.example.androidintermedieatesubmission.ui.viewmodel.AuthViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    companion object {
        const val PROFILE_KEY = "INTENT_PROFILE_KEY"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionbar = supportActionBar
        actionbar!!.title = getString(R.string.profile_users)
        actionbar.setDisplayHomeAsUpEnabled(true)

        val pref = AuthPreferences.getInstance(application.dataStore)

        var tokenViewModel = ViewModelProvider(this, ViewModelMainFactory(pref))[AuthViewModel::class.java]

        val dataUser = if (Build.VERSION.SDK_INT >= 33) {
            intent.getSerializableExtra(PROFILE_KEY, AuthData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(PROFILE_KEY) as AuthData
        }


        binding.tvName.text = dataUser?.nama
        Log.d("TESTVALUE", dataUser.toString())
        binding.btnLogout.setOnClickListener{
            AuthHelper.logOut(this, tokenViewModel)
        }
    }
}