package com.dicoding.storyapp.ui

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.Status
import com.dicoding.storyapp.data.local.UserPreference
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.databinding.ActivityMapsBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel: UserViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        val mUserPreference = UserPreference(this)
        val user = mUserPreference.getUser()
        user.token?.let {
            viewModel.getStoryByLocation(it).observe(this){
                when(it){
                    is Status.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Status.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val data = it.data
                        setLocation(data)
                    }
                    is Status.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, getString(R.string.error), Toast.LENGTH_LONG).show()
                    }
                    is Status.NotFound -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, getString(R.string.not_found), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        setMapStyle()

    }
    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e("MapsActivty", "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e("MapsActivty", "Can't find style. Error: ", exception)
        }
    }

    private fun setLocation(data: List<ListStoryItem>) {
        val defaultLocation = LatLng( data.first().lat, data.first().lon)
        data.forEach {
            val location = LatLng(it.lat, it.lon)
            mMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(it.name)
                    .snippet(it.description)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            )
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 5f))
    }
}