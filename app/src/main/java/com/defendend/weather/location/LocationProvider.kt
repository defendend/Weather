package com.defendend.weather.location

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject
import javax.inject.Singleton

private const val LATITUDE = "latitude"
private const val LONGITUDE = "longitude"
private const val LOCATION_FILE = "_location"

@Singleton
class LocationProvider @Inject constructor(@ApplicationContext private val context: Context) {
    private val _coordinates = MutableStateFlow(getInitialCoordinates())
    val coordinates = _coordinates.filterNotNull()

    fun setLocation(location: Pair<Double, Double>) {
        _coordinates.value = location
        saveLocationToPref(location)
    }

    private fun saveLocationToPref(location: Pair<Double, Double>) {
        val (lat, lon) = location

        setLatitude(latitude = lat.toString())
        setLongitude(longitude = lon.toString())

    }

    private fun getInitialCoordinates(): Pair<Double, Double>? {
        val lat = getLatitude()?.toDoubleOrNull()
        val lon = getLongitude()?.toDoubleOrNull()
        if (lat == null || lon == null) {
            return null
        }
        return lat to lon
    }

    private fun getLatitude(): String? {
        val pref = context.getSharedPreferences(
            context.applicationContext.packageName + LOCATION_FILE,
            Context.MODE_PRIVATE
        )
        return pref.getString(LATITUDE, "")
    }

    private fun getLongitude(): String? {
        val pref = context.getSharedPreferences(
            context.applicationContext.packageName + LOCATION_FILE,
            Context.MODE_PRIVATE
        )
        return pref.getString(LONGITUDE, "")
    }

    private fun setLatitude(latitude: String) {
        context.getSharedPreferences(
            context.applicationContext.packageName + LOCATION_FILE,
            Context.MODE_PRIVATE
        )
            .edit {
                putString(LATITUDE, latitude)
            }
    }

    private fun setLongitude(longitude: String) {
        context.getSharedPreferences(
            context.applicationContext.packageName + LOCATION_FILE,
            Context.MODE_PRIVATE
        )
            .edit {
                putString(LATITUDE, longitude)
            }
    }
}