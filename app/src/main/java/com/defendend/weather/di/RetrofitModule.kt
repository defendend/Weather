package com.defendend.weather.di

import com.defendend.weather.api.CityApi
import com.defendend.weather.api.WeatherApi
import com.defendend.weather.api.WeatherApi.Companion.API_URL
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton


private const val JSON_TYPE = "application/json"

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    private val json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherApi {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory(contentType = JSON_TYPE.toMediaType()))
            .baseUrl(API_URL)
            .build()
        return retrofit.create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCityApi(): CityApi {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory(contentType = JSON_TYPE.toMediaType()))
            .baseUrl(CityApi.CITY_API_URL)
            .build()
        return retrofit.create(CityApi::class.java)
    }
}