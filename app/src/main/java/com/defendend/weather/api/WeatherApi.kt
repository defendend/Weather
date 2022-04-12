package com.defendend.weather.api

import com.defendend.weather.api.WeatherApi.Constants.API_KEY
import com.defendend.weather.features.city.CityNameResponse
import com.defendend.weather.features.weather.WeatherResponseWrapper
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/2.5/onecall")
    suspend fun getWeatherFromGeolocation(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("exclude") exclude: String = "minutely",
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "ru",
        @Query("appid") API_KEY: String
    ): WeatherResponseWrapper

    @GET("data/2.5/onecall")
    suspend fun getWeatherFromCityName(
        @Query("q") cityName: String,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "ru",
        @Query("appid") API_KEY: String
    ): WeatherResponseWrapper

    @GET("geo/1.0/reverse")
    suspend fun getCityNameFromLocation(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("appid") appid: String = API_KEY
    ): CityNameResponse

    object Constants{
        const val API_KEY = "65026bbc1e33bb883cd79a2a0f8955b9"
        const val API_URL = "https://api.openweathermap.org/"
    }
}