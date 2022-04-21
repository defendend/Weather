package com.defendend.weather.ui.weather

import com.defendend.weather.location.LocationProvider
import com.defendend.weather.models.weather.LocationWeather
import com.defendend.weather.repository.WeatherRepository
import com.defendend.weather.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val locationProvider: LocationProvider,
    private val weatherRepository: WeatherRepository
) : BaseViewModel<WeatherState>() {

    override fun createInitialState(): WeatherState = WeatherState.Loading

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

    private suspend fun updateWeather(lat: Double, lon: Double) {

        weatherRepository.getWeather(
            lat = lat,
            lon = lon
        ).onSuccess {
            val state = createNewState(locationWeather = it)
            postState(state)
        }.onFailure {
        }
    }

    private fun createNewState(locationWeather: LocationWeather): WeatherState {
        return WeatherState.createFrom(locationWeather)
    }
}