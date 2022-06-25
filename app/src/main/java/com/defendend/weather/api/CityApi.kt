package com.defendend.weather.api

import com.defendend.weather.models.city.CitiesListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CityApi {

    @GET("geo/api.php")
    suspend fun getCityList(
        @Query("city_name") cityName: String,
        @Query("json") json: String = "",
        @Query("limit") exclude: Int = 10,
        @Query("api_key") apiKey: String = CITY_API_KEY
    ): CitiesListResponse

    companion object {
        const val CITY_API_URL = "https://htmlweb.ru/"
        const val CITY_API_KEY = "ac22db20d3f15439f6e2d59e8a48a597"
    }
}