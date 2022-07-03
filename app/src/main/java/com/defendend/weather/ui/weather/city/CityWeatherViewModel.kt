package com.defendend.weather.ui.weather.city

import com.defendend.weather.models.city.CityUi
import com.defendend.weather.repository.WeatherRepository
import com.defendend.weather.ui.base.UiEvent
import com.defendend.weather.ui.weather.base.WeatherEvent
import com.defendend.weather.ui.weather.base.WeatherState
import com.defendend.weather.ui.weather.current_location.BaseWeatherViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CityWeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : BaseWeatherViewModel(weatherRepository = weatherRepository) {

    private var city: CityUi? = null

    override fun createInitialState(): WeatherState = WeatherState.Loading

    override suspend fun handleEvent(event: UiEvent) {
        when (event) {
            is WeatherEvent.Refresh -> updateWeather()
            is WeatherEvent.CityParameter -> applyParameters(event = event)
        }
    }

    override suspend fun refreshWeather() {
        val mCity = city ?: return
        postState(WeatherState.Loading)
        updateWeather(
            lat = mCity.lat,
            lon = mCity.lon
        )
    }

    private suspend fun applyParameters(event: WeatherEvent.CityParameter) {
        city = event.city
        if (city == null) return
        updateWeather()
    }

    private suspend fun updateWeather() {
        val mCity = city ?: return
        postState(WeatherState.Loading)

        weatherRepository.updateWeatherForAdditionalCity(
            lat = mCity.lat,
            lon = mCity.lon,
            timeZone = mCity.timeZone,
            id = mCity.id
        ).onSuccess {
            val state = createNewState(locationWeather = it)
            postState(state)
        }.onFailure {
            println(it)
        }
    }
}