package com.defendend.weather.features.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherWrapper(
    @SerialName("coord")
    val coord: Coord,
    @SerialName("weather")
    val weather: List<Weather>,
    @SerialName("base")
    val base: String,
    @SerialName("main")
    val main: Main,
    @SerialName("visibility")
    val visibility: Int,
    @SerialName("wind")
    val wind: Wind,
    @SerialName("clouds")
    val clouds: Clouds,
    @SerialName("cod")
    val cod: Int,
    @SerialName("rain")
    val rain: Rain? = null,
    @SerialName("snow")
    val snow: Snow? = null,
    @SerialName("dt")
    val timestampUTC: Long,
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("sys")
    val sys: Sys,
    @SerialName("timezone")
    val timezone: Int
)

@Serializable
data class Weather(
    @SerialName("id")
    val id: Long,
    @SerialName("main")
    val main: String,
    @SerialName("description")
    val description: String,
    @SerialName("icon")
    val icon: String
)

@Serializable
data class Rain(
    @SerialName("1h")
    val oneHour: Double = 0.0,
    @SerialName("3h")
    val threeHours: Double = 0.0
)

@Serializable
data class Snow(
    @SerialName("1h")
    val oneHour: Double = 0.0,
    @SerialName("3h")
    val threeHours: Double = 0.0
)

@Serializable
data class Sys(
    @SerialName("type")
    val type: Int,
    @SerialName("id")
    val id: Long,
    @SerialName("message")
    val message: Double = 0.0,
    @SerialName("country")
    val country: String,
    @SerialName("sunrise")
    val sunrise: Int,
    @SerialName("sunset")
    val sunset: Int
)

@Serializable
data class Wind(
    @SerialName("speed")
    val speed: Double,
    @SerialName("deg")
    val deg: Int
)

@Serializable
data class Main(
    @SerialName("temp")
    val temp: Double,
    @SerialName("feels_like")
    val feels: Double,
    @SerialName("temp_min")
    val minTemp: Double,
    @SerialName("temp_max")
    val maxTemp: Double,
    @SerialName("pressure")
    val pressure: Int,
    @SerialName("humidity")
    val humidity: Int,
)

@Serializable
data class Clouds(
    @SerialName("all")
    val all: Int
)

@Serializable
data class Coord(
    @SerialName("lat")
    val latitude: Double,
    @SerialName("lon")
    val longitude: Double
)