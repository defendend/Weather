package com.defendend.weather

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity
data class WeatherCity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var name: String = "",
    var temperature: Int = 0,
    var windSpeed: Double = 0.0,
    val geolocation: Boolean = false
) {
}