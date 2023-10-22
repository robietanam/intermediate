package com.example.androidintermedieatesubmission

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import com.bangkit.navigationsubmission.data.preferences.AuthPreferences
import com.bangkit.navigationsubmission.data.preferences.dataStore
import com.bangkit.navigationsubmission.ui.viewmodel.ViewModelMainFactory
import com.example.androidintermedieatesubmission.databinding.ActivityMainBinding
import com.example.androidintermedieatesubmission.ui.viewmodel.AuthViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler().postDelayed({

            val pref = AuthPreferences.getInstance(application.dataStore)
            var tokenViewModel = ViewModelProvider(this, ViewModelMainFactory(pref))[AuthViewModel::class.java]

            tokenViewModel.getToken().observe(this){

                if (it.token != "" && it.token?.isNotEmpty() == true){
                    val intentDetail = Intent(this, ListStoriesActivity::class.java)
                    intentDetail.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intentDetail.putExtra(ListStoriesActivity.TOKEN_INTENT_KEY, it)
                    startActivity(intentDetail)
                } else {
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                            Pair(binding.imageView2, "image"),
                            Pair(binding.textView6, "title"),
                            Pair(binding.textView10, "desc"),
                        )
                    startActivity(Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }, optionsCompat.toBundle())
                    finish()
                }
            }

        }, 2000)

    }
}