package com.defendend.weather

import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

private const val REQUEST_CODE_GPS = 100

class MainActivity : AppCompatActivity(), LocationAdapter {

    private lateinit var locationManager: LocationManager
    private lateinit var myLocationListener: MyLocationListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        myLocationListener = MyLocationListener().apply {
            setLocationAdapter(this@MainActivity)
        }
        checkPermissions()

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (currentFragment == null) {
            val fragment =WeatherListFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_GPS && grantResults[0] == RESULT_OK) {
            checkPermissions()
        }
    }

    private fun checkPermissions() {
        if (ActivityCompat
                .checkSelfPermission(
                    this,
                    android.Manifest
                        .permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat
                .checkSelfPermission(
                    this,
                    android.Manifest
                        .permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_CODE_GPS
            )
        } else {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                30000,
                10f,
                myLocationListener
            )
        }
    }

    override fun onLocationChanged(location: Location) {

    }
}