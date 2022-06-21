package com.defendend.weather.ui.weather_list

import androidx.lifecycle.ViewModel
import com.defendend.weather.repository.CityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherListViewModel @Inject constructor(
    private val cityRepository: CityRepository
) : ViewModel() {


}