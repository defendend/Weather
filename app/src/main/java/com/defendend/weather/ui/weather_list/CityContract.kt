package com.defendend.weather.ui.weather_list

import com.defendend.weather.models.city.CityUi
import com.defendend.weather.ui.base.UiEffect
import com.defendend.weather.ui.base.UiState

sealed class WeatherListState : UiState {
    object Loading : WeatherListState()
    data class Data(
        val citiesUi: List<CityUi>
    ) : WeatherListState()

    companion object {
        fun createDataFromCities(cities: List<City>): Data {
            val citiesUi = cities.map {
                CityUi(
                    id = it.id,
                    name = it.cityName,
                    lat = it.lat,
                    lon = it.lon,
                    timeZone = it.timeZone
                )
            }
            return Data(citiesUi = citiesUi)
        }
    }
}

sealed class WeatherListEffect : UiEffect {
    object BadConnect : WeatherListEffect()
    data class UpdatePosition(
        val position: Int
    ) : WeatherListEffect()
}

