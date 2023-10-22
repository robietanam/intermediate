package com.example.androidintermedieatesubmission

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.androidintermedieatesubmission.databinding.ActivityAddStoryBinding
import com.example.androidintermedieatesubmission.helper.AuthData
import com.example.androidintermedieatesubmission.helper.getImageUri
import com.example.androidintermedieatesubmission.helper.requestPermissionLauncher
import com.example.androidintermedieatesubmission.helper.uriToFile
import com.example.androidintermedieatesubmission.ui.components.ButtonWithLoadingCustom
import com.example.androidintermedieatesubmission.ui.viewmodel.AddStoryViewModel
import com.example.androidintermedieatesubmission.ui.viewmodel.StoryViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import java.util.concurrent.TimeUnit


class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding

    private lateinit var myButton: ButtonWithLoadingCustom

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationRequest : LocationRequest

    private  lateinit var addStoryViewModel: AddStoryViewModel

    private var currentImageUri: Uri? = null

    private var locationToReq : Location? = null

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA

        private const val TAG = "MapsActivity"
        const val ADD_STORY_KEY = "ADD_STORY_KEY"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher(this, REQUIRED_PERMISSION)

        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionbar = supportActionBar
        actionbar!!.title = getString(R.string.add_story)
        actionbar.setDisplayHomeAsUpEnabled(true)

        myButton = binding.btnSubmit

        addStoryViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[AddStoryViewModel::class.java]

        val storyViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[StoryViewModel::class.java]

        val token = if (Build.VERSION.SDK_INT >= 33) {
            intent.getSerializableExtra(ADD_STORY_KEY, AuthData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(ADD_STORY_KEY) as AuthData
        }



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Log.d("tokennya", token.toString())

        addStoryViewModel.story.observe(this){ result ->
            setMyButtonEnable(true)
            Log.d("HASILNYAAPA", result.toString())
            if (result.error == true){
                Toast.makeText(this@AddStoryActivity, result.message, Toast.LENGTH_SHORT).show()
            } else {
                storyViewModel.getAll(token?.token ?: "")
                Toast.makeText(this@AddStoryActivity, result.message, Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

        addStoryViewModel.isLoading.observe(this){
            setMyButtonLoading(it)
        }


        binding.btnCamera.setOnClickListener {
            startCamera()
        }

        binding.btnGallery.setOnClickListener {
            startGallery()
        }

        binding.btnSubmit.setOnClickListener {
            uploadImage(token?.token ?: "")
        }


        binding.checkLoc.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                createLocationRequest()
            }
        }


    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }
                else -> {
                    // No location access granted.
                }
            }
        }

    private val resolutionLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            when (result.resultCode) {
                RESULT_OK ->
                    Log.i(TAG, "onActivityResult: All location settings are satisfied.")
                RESULT_CANCELED ->
                    Toast.makeText(
                        this,
                        "Anda harus mengaktifkan GPS untuk menggunakan aplikasi ini!",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }

    private fun createLocationRequest(){

        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(1)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getMyLastLocation()
            }
            .addOnFailureListener { exception ->
                binding.checkLoc.isChecked = false
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Toast.makeText(this, sendEx.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun getMyLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
        val mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    if (location != null) {
                        //TODO: UI updates.
                    }
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null)

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->

                if (location != null) {
                    locationToReq = location
                    Toast.makeText(
                        this@AddStoryActivity,
                        "${location.toString()}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    binding.checkLoc.isChecked = false
                    Toast.makeText(
                        this@AddStoryActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }


    private fun setMyButtonLoading(value: Boolean){
        setMyButtonEnable(false)
        myButton.setLoading(value)
    }

    private fun setMyButtonEnable(value: Boolean) {
        myButton.isEnabled = value
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("ImageURI", "showImage: $it")
            binding.imgPreview.setImageURI(it)
        }
    }


    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }


    private fun uploadImage(token: String) {

        if (currentImageUri != null && binding.editDesc.text.isNotEmpty()){
            setMyButtonLoading(true)
            currentImageUri?.let { uri ->
                val imageFile = uriToFile(uri, this)
                Log.d("ImageFile", "showImage: ${imageFile.path}")
                val description =  binding.editDesc.text.toString()
                if (!binding.checkLoc.isChecked){
                    locationToReq = null
                }

                addStoryViewModel.uploadImage(imageFile, description, lon = locationToReq?.longitude, lat = locationToReq?.latitude,
                    token = token
                )
            }
        } else {
            Log.d("HASILNYAAPA", "${currentImageUri}  ${binding.editDesc.text.isNotEmpty()}")
            Toast.makeText(this@AddStoryActivity, "Silahkan pilih gambar dan isi deskripsi", Toast.LENGTH_SHORT).show()
        }

    }




}