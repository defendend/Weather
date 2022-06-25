package com.defendend.weather.ui.weather_list

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class City(
    @PrimaryKey val name: String,
    val cityName: String,
    val temperature: String,
    val timeZone: String,
    val weatherInfo: String,
    val minTemp: Int,
    val maxTemp: Int
)