package com.defendend.weather.ui.weather.current_location

import com.defendend.weather.models.weather.LocationWeather
import com.defendend.weather.repository.WeatherRepository
import com.defendend.weather.ui.base.BaseViewModel
import com.defendend.weather.ui.base.UiEvent
import com.defendend.weather.ui.weather.base.WeatherEvent
import com.defendend.weather.ui.weather.base.WeatherState


abstract class BaseWeatherViewModel(
    private val weatherRepository: WeatherRepository
) : BaseViewModel<WeatherState>() {

    override fun createInitialState(): WeatherState = WeatherState.Loading



    override suspend fun handleEvent(event: UiEvent) {
        when (event) {
            is WeatherEvent.Refresh -> refreshWeather()
        }
    }

    abstract suspend fun refreshWeather()

    protected suspend fun updateWeather(lat: Double, lon: Double) {
        weatherRepository.updateWeatherForCity(
            lat = lat,
            lon = lon
        ).onSuccess {
            val state = createNewState(locationWeather = it)
            postState(state)
        }.onFailure {
            println(it)
        }
    }

    fun createNewState(locationWeather: LocationWeather): WeatherState {
        return WeatherState.createFrom(locationWeather)
    }
}