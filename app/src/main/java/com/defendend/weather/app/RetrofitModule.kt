package com.defendend.weather.app

import com.defendend.weather.api.WeatherApi
import com.defendend.weather.features.weather.WeatherResponseWrapper
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton

private val json = Json { ignoreUnknownKeys = true }
private val contentType = "application/json".toMediaType()

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory(contentType = contentType))
        .baseUrl(WeatherApi.Constants.API_URL)
        .build()

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit): WeatherApi = retrofit.create(WeatherApi::class.java)

}