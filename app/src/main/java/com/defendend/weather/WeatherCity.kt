package com.defendend.weather

data class WeatherCity (
    val name: String,
    var temperature: Int,
    var geolocation: Boolean = false
) {
}