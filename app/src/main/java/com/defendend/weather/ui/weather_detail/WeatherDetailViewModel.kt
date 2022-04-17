package com.defendend.weather.ui.weather_detail
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.Transformations
//import androidx.lifecycle.ViewModel
//import com.defendend.weather.models.city.WeatherCity
//import com.defendend.weather.repository.WeatherRepository
//import java.util.*
//
//class WeatherDetailViewModel : ViewModel() {
//
//    private val weatherRepository = WeatherRepository.get()
//    private val weatherIdLiveData = MutableLiveData<UUID>()
//
//    var weatherLiveData: LiveData<WeatherCity?> =
//        Transformations.switchMap(weatherIdLiveData) { weatherId ->
//            weatherRepository.getWeather(weatherId)
//        }
//
//    fun loadWeatherCity(weatherId: UUID) {
//        weatherIdLiveData.value = weatherId
//    }
//
//    fun saveWeatherCity(weatherCity: WeatherCity) {
//        weatherRepository.updateWeather(weatherCity)
//    }
//
//}