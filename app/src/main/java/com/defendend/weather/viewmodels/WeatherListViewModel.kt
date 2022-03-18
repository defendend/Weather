package com.defendend.weather.viewmodels

import androidx.lifecycle.ViewModel
import com.defendend.weather.WeatherCity
import com.defendend.weather.repository.WeatherRepository

class WeatherListViewModel : ViewModel() {

    private val weatherRepository = WeatherRepository.get()
    val weatherListLiveData = weatherRepository.getWeathers()

    fun addWeatherCity(weatherCity: WeatherCity) {
        weatherRepository.addWeatherCity(weatherCity)
    }
}