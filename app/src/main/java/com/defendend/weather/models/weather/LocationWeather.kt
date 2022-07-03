package com.defendend.weather.models.weather

data class LocationWeather(
    val currentCity: String,
    val cityNameEn: String,
    val currentTemperature: String,
    val description: String,
    val minTemp: Int,
    val maxTemp: Int,
    val tomorrowInfo: Triple<Boolean, Boolean, Int>,
    val hourly: List<Hourly>,
    val daily: List<Daily>,
    val cardItems: List<TileItem>
)