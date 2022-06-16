package com.defendend.weather.ui.weather

import com.defendend.weather.models.weather.Daily
import com.defendend.weather.models.weather.Hourly
import com.defendend.weather.models.weather.LocationWeather
import com.defendend.weather.models.weather.TileItem
import com.defendend.weather.ui.base.UiEffect
import com.defendend.weather.ui.base.UiEvent
import com.defendend.weather.ui.base.UiState


sealed class WeatherEvent : UiEvent {
    object Refresh : WeatherEvent()
}

sealed class WeatherState : UiState {
    object Loading : WeatherState()
    data class Data(
        val currentCity: String,
        val currentTemperature: String,
        val description: String,
        val minTemp: Int,
        val maxTemp: Int,
        val tomorrowInfo: Triple<Boolean, Boolean, Int>,
        val hourly: List<Hourly>,
        val daily: List<Daily>,
        val cardItems: List<TileItem>
    ) : WeatherState()

    companion object {
        fun createFrom(weather: LocationWeather): Data {
            return Data(
                currentCity = weather.currentCity,
                currentTemperature = weather.currentTemperature,
                description = weather.description,
                minTemp = weather.minTemp,
                maxTemp = weather.maxTemp,
                tomorrowInfo = weather.tomorrowInfo,
                hourly = weather.hourly,
                daily = weather.daily,
                cardItems = weather.cardItems
            )
        }
    }

}

sealed class WeatherEffect : UiEffect {
    object BadConnect : WeatherEffect()
}
