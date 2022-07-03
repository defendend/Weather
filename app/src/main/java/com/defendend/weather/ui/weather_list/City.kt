package com.defendend.weather.ui.weather_list

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class City(
    @PrimaryKey val id: String,
    val cityName: String,
    val cityNameEn: String,
    val lat: Double,
    val lon: Double,
    val temperature: String,
    val timeZone: String,
    val weatherInfo: String,
    val minTemp: Int,
    val maxTemp: Int
)