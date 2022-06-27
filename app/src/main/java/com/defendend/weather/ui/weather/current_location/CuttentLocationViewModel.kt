package com.defendend.weather.ui.weather.current_location

import com.defendend.weather.location.LocationProvider
import com.defendend.weather.repository.WeatherRepository
import com.defendend.weather.ui.weather.base.WeatherState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CurrentLocationViewModel @Inject constructor(
    private val locationProvider: LocationProvider,
    weatherRepository: WeatherRepository
) : BaseWeatherViewModel(
    weatherRepository = weatherRepository
) {

    init {
        safeIoLaunch {
            observeLocation()
        }
    }

    private fun observeLocation() = safeIoLaunch {
        locationProvider.coordinates.collect {
            val (lat, lon) = it

            updateWeather(
                lat = lat,
                lon = lon
            )
        }
    }

    override suspend fun refreshWeather() {
        val coordinates = locationProvider.takeCurrentCoordinates()
        if (coordinates != null) {
            postState(WeatherState.Loading)

            val (lat, lon) = coordinates
            updateWeather(
                lat = lat,
                lon = lon
            )
        }
    }
}