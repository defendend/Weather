package com.defendend.weather

import android.app.Application
import com.defendend.weather.repository.CityRepository

class WeatherApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CityRepository.initialize(this)
    }
}