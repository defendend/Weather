package com.defendend.weather

import android.location.Location
import android.location.LocationListener

class MyLocationListener : LocationListener {

    private lateinit var locationAdapter: LocationAdapter

    override fun onLocationChanged(location: Location) {
        locationAdapter.onLocationChanged(location)
    }

    fun setLocationAdapter(locationAdapter: LocationAdapter){
        this.locationAdapter = locationAdapter
    }
}