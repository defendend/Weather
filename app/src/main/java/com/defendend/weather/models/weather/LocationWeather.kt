package com.defendend.weather.models.weather

data class LocationWeather(
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
)