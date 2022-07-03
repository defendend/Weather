package com.defendend.weather.ui.weather_list

import com.defendend.weather.preference.WeatherListPreference
import com.defendend.weather.repository.CityRepository
import com.defendend.weather.ui.base.BaseViewModel
import com.defendend.weather.ui.base.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val DEFAULT_CITY = "default"

@HiltViewModel
class WeatherListViewModel @Inject constructor(
    private val cityRepository: CityRepository,
    private val weatherListPreference: WeatherListPreference
) : BaseViewModel<WeatherListState>() {

    override fun createInitialState(): WeatherListState = WeatherListState.Loading

    init {
        safeIoLaunch {
            observeCities()
        }
    }

    override suspend fun handleEvent(event: UiEvent) {
        when (event) {
            is WeatherListEvent.Position -> updatePosition(event = event)
        }
    }

    private fun updatePosition(event: WeatherListEvent.Position) {
        weatherListPreference.setPosition(event.position)
    }

    private suspend fun observeCities() {
        cityRepository.citiesFlow()
            .map { cities -> cities.filter { it.id != DEFAULT_CITY } }
            .combine(weatherListPreference.position) { cities, position ->
                WeatherListState.createDataFromCities(
                    selectedPosition = position,
                    cities = cities
                )
            }.collect { postState(state = it) }
    }
}