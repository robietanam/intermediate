package com.example.androidintermedieatesubmission

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.androidintermedieatesubmission.databinding.ActivityDetailStoryBinding
import com.example.androidintermedieatesubmission.helper.DetailData
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


class DetailStoryActivity : AppCompatActivity() {

    companion object {
        const val DETAIL_INTENT_KEY = "DETAIL STORY"
    }

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionbar = supportActionBar
        actionbar!!.title = getString(R.string.detail_story)
        actionbar.setDisplayHomeAsUpEnabled(true)

        val detailData = if (Build.VERSION.SDK_INT >= 33) {
            intent.getSerializableExtra(DETAIL_INTENT_KEY, DetailData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(DETAIL_INTENT_KEY) as DetailData
        }
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").apply {
            timeZone = TimeZone.getTimeZone("GMT")
        }
        val localDateTime = formatter.parse(detailData?.time)


        val formatterToString = SimpleDateFormat("EEEE, dd MMMM yyyy '('HH:mm')'", Locale("id", "ID"))
        binding.tvName.text = detailData?.nama
        binding.tvDescription.text = detailData?.description
        binding.tvCreatedat.text = formatterToString.format(localDateTime)

        binding.imgUser.setOnClickListener {
            val intentDetail = Intent(this, ViewImageActivity::class.java)
            intentDetail.putExtra(ViewImageActivity.TAG, detailData?.image)

            startActivity(intentDetail)
        }

        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(), RoundedCorners(16))

        Glide.with(binding.root)
            .load(detailData?.image)
            .apply(requestOptions)
            .into(binding.imgUser)
    }
}