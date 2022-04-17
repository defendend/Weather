package com.defendend.weather.ui.base

import com.defendend.weather.models.weather.Daily
import com.defendend.weather.models.weather.Hourly


sealed class WeatherEvent : UiEvent {
    data class Refresh(
        val lat: Double,
        val lon: Double
    ) : WeatherEvent()
    object ShowWeather : WeatherEvent()
}

sealed class WeatherState : UiState {
    object Loading : WeatherState()
    data class Data(
        val currentCity: String,
        val currentTemperature: String,
        val description: String,
        val minTemp: Int,
        val maxTemp: Int,
        val tomorrowInfo: Pair<Pair<Boolean, Boolean>, Int>,
        val hourly: List<Hourly>,
        val daily: List<Daily>,
        val uvIndex: String,
        val uvIndexLevel: Int,
        val uvIndexDescription: Pair<Int,Int>,
        val sunrise: Long,
        val sunset: Long,
        val windSpeed: Int,
        val windGust: Int,
        val windDirection: Int,
        val precipitation: String,
        val feelsLike: String,
        val feelsLikeDescription: Int,
        val pressureMm: String,
        val humidity: Int,
        val dewPoint: String,
        val visibility: Int
    ) : WeatherState()
}

sealed class WeatherEffect : UiEffect {
    object BadConnect : WeatherEffect()
}
