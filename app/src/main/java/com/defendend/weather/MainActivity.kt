package com.defendend.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.defendend.weather.MainActivity.LocationConstants.DISTANCE_TO_NEW_LOCATION_M
import com.defendend.weather.MainActivity.LocationConstants.REQUEST_CODE_GPS
import com.defendend.weather.MainActivity.LocationConstants.TIME_TO_NEW_LOCATION_MS
import com.defendend.weather.location.LocationProvider
import com.defendend.weather.ui.weather_list.WeatherListFragment
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "JsonWeather"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var locationManager: LocationManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var locationProvider: LocationProvider
    private val scope = CoroutineScope(Dispatchers.IO)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        checkUserPermissions()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (currentFragment == null) {
            val fragment = WeatherListFragment.newInstance()
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

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                fusedLocationProviderClient.lastLocation
                    .addOnCompleteListener(OnCompleteListener<Location?> { task ->
                        val location = task.result
                        if (location == null) {
                            requestNewLocationData()
                        } else {
                            locationProvider.setLocation(location.latitude to location.longitude)
                        }
                    })
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            askUserForPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_GPS && grantResults[0] == RESULT_OK) {
            checkUserPermissions()
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkPermissions()) {
            getLastLocation()
        }
    }

    private fun isPermissionGranted(permission: String): Boolean {
        val result = ActivityCompat.checkSelfPermission(this, permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        // setting LocationRequest
        // on FusedLocationClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.getMainLooper()
        )
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation = locationResult.lastLocation
            if (mLastLocation != null) {
                locationProvider.setLocation(mLastLocation.latitude to mLastLocation.longitude)
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun checkUserPermissions() {
        val hasPermission = isPermissionGranted(android.Manifest.permission.ACCESS_FINE_LOCATION)
                && isPermissionGranted(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                && isPermissionGranted(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        if (!hasPermission) {
            askUserForPermissions()
        } else {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                TIME_TO_NEW_LOCATION_MS,
                DISTANCE_TO_NEW_LOCATION_M,
                this
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
        const val DISTANCE_TO_NEW_LOCATION_M = 2_000f
        const val TIME_TO_NEW_LOCATION_MS = 3_600_000L
    }
}