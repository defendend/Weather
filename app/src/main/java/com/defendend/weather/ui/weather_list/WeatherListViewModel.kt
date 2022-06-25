package com.defendend.weather.ui.weather_list

import com.defendend.weather.repository.CityRepository
import com.defendend.weather.ui.base.BaseViewModel
import com.defendend.weather.ui.base.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherListViewModel @Inject constructor(
    private val cityRepository: CityRepository
) : BaseViewModel<WeatherListState>() {

    override fun createInitialState(): WeatherListState = WeatherListState.Loading

    init {
        safeIoLaunch {
            observeCities()
        }
    }

    override suspend fun handleEvent(event: UiEvent) {

    }

    private suspend fun observeCities() {
        cityRepository.citiesFlow().collect { cities ->
            val cityNameList = cities.map { it.name }
            val state = WeatherListState.Data(cityNameList = cityNameList)
            postState(state)
        }
    }

}