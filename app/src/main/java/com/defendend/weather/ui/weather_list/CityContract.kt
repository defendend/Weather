package com.defendend.weather.ui.weather_list

import com.defendend.weather.models.city.CityUi
import com.defendend.weather.ui.base.UiEffect
import com.defendend.weather.ui.base.UiEvent
import com.defendend.weather.ui.base.UiState

sealed class WeatherListEvent : UiEvent {
    data class Position(
        val position: Int
    ) : WeatherListEvent()
}

sealed class WeatherListState : UiState {
    object Loading : WeatherListState()
    data class Data(
        val selectedPosition: Int,
        val citiesUi: List<CityUi>
    ) : WeatherListState()

    companion object {
        fun createDataFromCities(selectedPosition: Int, cities: List<City>): Data {
            val citiesUi = cities.map {
                CityUi(
                    id = it.id,
                    name = it.cityName,
                    lat = it.lat,
                    lon = it.lon,
                    timeZone = it.timeZone
                )
            }
            return Data(selectedPosition = selectedPosition, citiesUi = citiesUi)
        }
    }
}

sealed class WeatherListEffect : UiEffect {
    object BadConnect : WeatherListEffect()
    data class UpdatePosition(
        val position: Int
    ) : WeatherListEffect()
}

