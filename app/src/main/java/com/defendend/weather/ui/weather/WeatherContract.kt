package com.defendend.weather.ui.weather

import com.defendend.weather.models.weather.Daily
import com.defendend.weather.models.weather.Hourly
import com.defendend.weather.models.weather.LocationWeather
import com.defendend.weather.ui.base.UiEffect
import com.defendend.weather.ui.base.UiEvent
import com.defendend.weather.ui.base.UiState


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
        val tomorrowInfo: Triple<Boolean, Boolean, Int>,
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

    companion object {
        fun createFrom(weather: LocationWeather) : Data{
            return Data(
                currentCity = weather.currentCity,
                currentTemperature = weather.currentTemperature,
                description = weather.description,
                minTemp = weather.minTemp,
                maxTemp = weather.maxTemp,
                tomorrowInfo = weather.tomorrowInfo,
                hourly = weather.hourly,
                daily = weather.daily,
                uvIndex = weather.uvIndex,
                uvIndexLevel = weather.uvIndexLevel,
                uvIndexDescription = weather.uvIndexDescription,
                sunrise = weather.sunrise,
                sunset = weather.sunset,
                windSpeed = weather.windSpeed,
                windGust = weather.windGust,
                windDirection = weather.windDirection,
                precipitation = weather.precipitation,
                feelsLike = weather.feelsLike,
                feelsLikeDescription = weather.feelsLikeDescription,
                pressureMm = weather.pressureMm,
                humidity = weather.humidity,
                dewPoint = weather.dewPoint,
                visibility = weather.humidity
            )
        }
    }

}

sealed class WeatherEffect : UiEffect {
    object BadConnect : WeatherEffect()
}
