package com.defendend.weather.features.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherWrapper(
    @SerialName("base")
    val base: String,
    @SerialName("clouds")
    val clouds: Clouds,
    @SerialName("cod")
    val cod: Int,
    @SerialName("coord")
    val coord: Coord,
    @SerialName("dt")
    val timestampUTC: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("main")
    val main: Main,
    @SerialName("name")
    val name: String,
    @SerialName("sys")
    val sys: Sys,
    @SerialName("timezone")
    val timezone: Int,
    @SerialName("visibility")
    val visibility: Int,
    @SerialName("weather")
    val weather: List<Weather>,
    @SerialName("Wind")
    val wind: Wind
)

@Serializable
data class Weather(
    @SerialName("id")
    val id: Int,
    @SerialName("main")
    val main: String,
    @SerialName("description")
    val description: String,
    @SerialName("icon")
    val icon: String
)

@Serializable
data class Sys(
    @SerialName("type")
    val type: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("message")
    val message: Double,
    @SerialName("country")
    val country: Int,
    @SerialName("sunrise")
    val sunrise: Long,
    @SerialName("sunset")
    val sunset: Long
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