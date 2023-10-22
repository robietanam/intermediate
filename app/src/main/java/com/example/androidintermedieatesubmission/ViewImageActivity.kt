package com.example.androidintermedieatesubmission

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.androidintermedieatesubmission.databinding.ActivityViewImageBinding

class ViewImageActivity : AppCompatActivity() {

    companion object {
        const val TAG = "VIEW_IMAGE_TAG"
    }
    private lateinit var binding: ActivityViewImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageData = intent.getStringExtra(TAG)

        Glide.with(binding.root)
            .load(imageData)
            .into(binding.imgPreview)
    }
}