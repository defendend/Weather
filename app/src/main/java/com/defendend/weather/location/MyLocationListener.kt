package com.defendend.weather.location

import android.location.Location
import android.location.LocationListener
import com.defendend.weather.location.LocationAdapter

class MyLocationListener : LocationListener {

    private lateinit var locationAdapter: LocationAdapter

    override fun onLocationChanged(location: Location) {
        locationAdapter.onLocationChanged(location)
    }

    fun setLocationAdapter(locationAdapter: LocationAdapter){
        this.locationAdapter = locationAdapter
    }
}