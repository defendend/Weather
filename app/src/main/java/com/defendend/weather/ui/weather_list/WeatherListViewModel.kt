package com.defendend.weather.ui.weather_list

import com.defendend.weather.preference.WeatherListPreference
import com.defendend.weather.repository.CityRepository
import com.defendend.weather.ui.base.BaseViewModel
import com.defendend.weather.ui.base.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
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

    }

    private suspend fun observeCities() {
        cityRepository.citiesFlow().collect { cities ->
            val citiesWithoutDefault = cities.filter { it.id != DEFAULT_CITY }
            val state = WeatherListState.createDataFromCities(cities = citiesWithoutDefault)
            val position = weatherListPreference.getPosition()
            val effect = WeatherListEffect.UpdatePosition(position = position)
            postState(state = state)
            postEffect(effect = effect)
        }
    }
}