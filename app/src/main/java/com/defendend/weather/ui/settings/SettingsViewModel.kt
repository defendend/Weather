package com.defendend.weather.ui.settings

import com.defendend.weather.database.CityDao
import com.defendend.weather.models.city.CityUi
import com.defendend.weather.repository.CityNameRepository
import com.defendend.weather.repository.WeatherRepository
import com.defendend.weather.ui.base.BaseViewModel
import com.defendend.weather.ui.base.UiEvent
import com.defendend.weather.ui.weather_list.City
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val cityNameRepository: CityNameRepository,
    private val weatherRepository: WeatherRepository,
    private val cityDao: CityDao
) : BaseViewModel<SettingsState>() {
    override fun createInitialState(): SettingsState = SettingsState.Loading
    private val query = MutableStateFlow("")

    init {
        observeQuery()
        observeCities()
    }

    override suspend fun handleEvent(event: UiEvent) {
        when (event) {
            is SettingsEvent.Query -> query.value = event.cityName
            is SettingsEvent.OnCityClick -> handleCityClick(event = event)
            is SettingsEvent.OnCloseSearch -> onCloseSearch()
        }
    }

    private fun observeQuery() = safeIoLaunch {
        query.debounce(500)
            .distinctUntilChanged()
            .collect {
                if (it.isEmpty()) {
                    val cities = cityDao.getCities()
                    postState(SettingsState.Data(cities = cities))
                } else {
                    updateCities(it)
                }
            }
    }

    private fun observeCities() = safeIoLaunch {
        val cities = cityDao.getCities()
        postState(SettingsState.Data(cities = cities))
    }

    private suspend fun updateCities(cityName: String) {
        cityNameRepository.getCitiesNames(cityName = cityName)
            .onSuccess { cities ->
                val citiesUi = cities.map { it.convertToUi() }
                val state = SettingsState.SearchCity(cityNames = citiesUi)
                postState(state = state)
            }
            .onFailure {
                println(it)
            }
    }

    private suspend fun handleCityClick(event: SettingsEvent.OnCityClick) {
        postState(SettingsState.Loading)
        addNewCity(cityUi = event.cityUi)
        val cities = cityDao.getCities()
        postState(SettingsState.Data(cities = cities))
    }

    private suspend fun addNewCity(cityUi: CityUi) {
        weatherRepository.updateWeatherForAdditionalCity(
            lat = cityUi.lat,
            lon = cityUi.lon,
            timeZone = cityUi.timeZone
        )
    }

    private suspend fun onCloseSearch() {
        val cities = cityDao.getCities()
        postState(SettingsState.Data(cities = cities))
    }
}