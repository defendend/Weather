package com.defendend.weather.api

import com.defendend.weather.features.weather.WeatherWrapper
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/2.5/weather")
    suspend fun getWeatherFromGeolocation(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "ru",
        @Query("appid") API_KEY: String
    ): WeatherWrapper

    @GET("data/2.5/weather")
    suspend fun getWeatherFromCityName(
        @Query("q") cityName: String,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "ru",
        @Query("appid") API_KEY: String
    ): WeatherWrapper
    object Constants {
        const val API_KEY = "65026bbc1e33bb883cd79a2a0f8955b9"
        const val API_URL = "https://api.openweathermap.org/"
    }
}