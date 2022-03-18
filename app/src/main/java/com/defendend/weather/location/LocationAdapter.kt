package com.defendend.weather.location

import android.location.Location

interface LocationAdapter {

    fun onLocationChanged(location: Location)
}