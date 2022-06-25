package com.defendend.weather.ui.weather.city

import com.defendend.weather.repository.WeatherRepository
import com.defendend.weather.ui.base.UiEvent
import com.defendend.weather.ui.base.BaseViewModel
import com.defendend.weather.ui.weather.base.WeatherEvent
import com.defendend.weather.ui.weather.base.WeatherState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CityWeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : BaseViewModel<WeatherState>() {

    private var cityName: String = ""

    override fun createInitialState(): WeatherState = WeatherState.Loading

    override suspend fun handleEvent(event: UiEvent) {
        when (event) {
            is WeatherEvent.Refresh -> updateWeather()
            is WeatherEvent.CityParameter -> applyParameters(event = event)
        }
    }

    private suspend fun applyParameters(event: WeatherEvent.CityParameter) {
        cityName = event.name
        updateWeather()
    }

    private suspend fun updateWeather() {
        if (cityName.isEmpty()) return

        postState(WeatherState.Loading)

        //postState()
    }
}