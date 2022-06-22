package com.defendend.weather.ui.weather.current_location

import com.defendend.weather.location.LocationProvider
import com.defendend.weather.models.weather.LocationWeather
import com.defendend.weather.repository.WeatherRepository
import com.defendend.weather.ui.base.UiEvent
import com.defendend.weather.ui.weather.base.BaseViewModel
import com.defendend.weather.ui.weather.base.WeatherEvent
import com.defendend.weather.ui.weather.base.WeatherState
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

    override suspend fun handleEvent(event: UiEvent) {
        when (event) {
            is WeatherEvent.Refresh -> refreshWeather()
        }
    }

    private suspend fun refreshWeather() {
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
            println(it)
        }
    }

    private fun createNewState(locationWeather: LocationWeather): WeatherState {
        return WeatherState.createFrom(locationWeather)
    }
}