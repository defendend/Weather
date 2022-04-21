package com.defendend.weather.api

import com.defendend.weather.models.city.CityNameResponse
import com.defendend.weather.models.weather.WeatherResponseWrapper
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/2.5/onecall")
    suspend fun getWeatherFromGeolocation(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("exclude") exclude: String = "minutely",
        @Query("units") units: String = "metric",
        @Query("lang") language: String,
        @Query("appid") appid: String = API_KEY
    ): WeatherResponseWrapper

    @GET("data/2.5/onecall")
    suspend fun getWeatherFromCityName(
        @Query("q") cityName: String,
        @Query("units") units: String = "metric",
        @Query("lang") language: String,
        @Query("appid") appid: String = API_KEY
    ): WeatherResponseWrapper

    @GET("geo/1.0/reverse")
    suspend fun getCityNameFromLocation(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("appid") appid: String = API_KEY
    ): CityNameResponse

    companion object{
        private const val API_KEY = "65026bbc1e33bb883cd79a2a0f8955b9"
        const val API_URL = "https://api.openweathermap.org/"
        const val TAG_RU = "ru"
        const val TAG_EN = "en"
    }
}