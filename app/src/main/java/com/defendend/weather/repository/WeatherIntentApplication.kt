package com.defendend.weather.repository

import android.app.Application

class WeatherIntentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        WeatherRepository.initialize(this)
    }
}