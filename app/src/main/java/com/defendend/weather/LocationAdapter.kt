package com.defendend.weather

import android.location.Location

interface LocationAdapter {

    fun onLocationChanged(location: Location)
}