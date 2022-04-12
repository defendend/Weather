package com.defendend.weather

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.defendend.weather.MainActivity.LocationConstants.DISTANCE_TO_NEW_LOCATION_M
import com.defendend.weather.MainActivity.LocationConstants.REQUEST_CODE_GPS
import com.defendend.weather.MainActivity.LocationConstants.TIME_TO_NEW_LOCATION_MS
import com.defendend.weather.fragments.WeatherFragment
import com.defendend.weather.location.LocationAdapter
import com.defendend.weather.location.LocationProvider
import com.defendend.weather.location.MyLocationListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "JsonWeather"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), LocationAdapter {

    private lateinit var locationManager: LocationManager
    private lateinit var myLocationListener: MyLocationListener

    @Inject
    lateinit var locationProvider: LocationProvider
    private val scope = CoroutineScope(Dispatchers.IO)


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
            val fragment = WeatherFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit()
        }

    }


    override fun onLocationChanged(location: Location) {
        scope.launch {
            val lat = location.latitude
            val lon = location.longitude
            locationProvider.setLocation(lat to lon)
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
            askUserForPermissions()
        } else {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                TIME_TO_NEW_LOCATION_MS,
                DISTANCE_TO_NEW_LOCATION_M,
                myLocationListener
            )
        }
    }

    private fun askUserForPermissions() {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_CODE_GPS
        )
    }

    object LocationConstants {
        const val REQUEST_CODE_GPS = 100
        const val DISTANCE_TO_NEW_LOCATION_M = 2_000_0f
        const val TIME_TO_NEW_LOCATION_MS = 3_600_000L
    }
}