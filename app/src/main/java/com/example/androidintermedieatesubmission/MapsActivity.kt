package com.example.androidintermedieatesubmission

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.example.androidintermedieatesubmission.data.response.StoryResponse
import com.example.androidintermedieatesubmission.data.response.StoryResponseItem
import com.example.androidintermedieatesubmission.databinding.ActivityMapsBinding
import com.example.androidintermedieatesubmission.helper.AuthData
import com.example.androidintermedieatesubmission.helper.DetailData
import com.example.androidintermedieatesubmission.ui.viewmodel.StoryViewModel
import com.example.androidintermedieatesubmission.ui.viewmodel.ViewModelFactoryStory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var storyViewModel: StoryViewModel
    private  lateinit var story: StoryResponse

    private var token: AuthData? = null

    companion object {
        const val MAPS_ACTIVITY_INTENT_KEY = "KEY_MAPS"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storyViewModel = ViewModelProvider(this, ViewModelFactoryStory(this, token?.token))[StoryViewModel::class.java]

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        token = if (Build.VERSION.SDK_INT >= 33) {
            intent.getSerializableExtra(MAPS_ACTIVITY_INTENT_KEY, AuthData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(MAPS_ACTIVITY_INTENT_KEY) as AuthData
        }

        storyViewModel.getAll(token?.token ?: "")

        storyViewModel.story.observe(this){
            if (it != null){
                Log.d("ISERROR", it.toString())
                if (it.error == true){
                    Toast.makeText(this, "Kredensial invalid, tolong login kembali", Toast.LENGTH_LONG).show()

                } else {
                    story = it
                    populateMarker(story)
                }

            } else {
                Toast.makeText(this, "Ada Error", Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                mMap.setMapStyle(null)
                true
            }
            R.id.retro_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.retro_map_style))
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        val data = storyViewModel.story.value
        if (data != null) {
            populateMarker(data)

        }

        val initialLoc = LatLng(-6.8957643, 107.6338462)

        mMap.setOnMarkerClickListener {
            mMap.animateCamera((CameraUpdateFactory.newLatLngZoom(it.position, 15f)))
            it.showInfoWindow()
            true
        }

        mMap.setOnInfoWindowClickListener {
            val markerData = it.tag as StoryResponseItem
            val intentDetail = Intent(this, DetailStoryActivity::class.java)
            intentDetail.putExtra(DetailStoryActivity.DETAIL_INTENT_KEY, DetailData(nama = markerData.name!!, image = markerData.photoUrl!!, description = markerData.description!!, time = markerData.createdAt!! ))
            startActivity(intentDetail)
            true
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialLoc, 5f))

        getMyLocation()

    }

    private fun populateMarker(story: StoryResponse){
        story.listStory.forEach { data ->
            Log.d("DATANYAPAA", data.toString())
            val latLng = LatLng(data.lat?.toDouble() ?: 0.0, data.lon?.toDouble() ?: 0.0)

            mMap.addMarker(MarkerOptions()
                .position(latLng)
                .title(data.name)
                .icon(vectorToBitmap(R.drawable.custom_pin))
                .snippet(data.description))?.apply {
                tag = data
            }
        }
    }


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun vectorToBitmap(@DrawableRes id: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}