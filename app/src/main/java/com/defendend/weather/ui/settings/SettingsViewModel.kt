package com.defendend.weather.ui.settings

import com.defendend.weather.models.city.CityUi
import com.defendend.weather.preference.WeatherListPreference
import com.defendend.weather.repository.CityNameRepository
import com.defendend.weather.repository.CityRepository
import com.defendend.weather.repository.WeatherRepository
import com.defendend.weather.ui.base.BaseViewModel
import com.defendend.weather.ui.base.UiEvent
import com.defendend.weather.ui.weather_list.City
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val cityNameRepository: CityNameRepository,
    private val weatherRepository: WeatherRepository,
    private val cityRepository: CityRepository,
    private val weatherListPreference: WeatherListPreference
) : BaseViewModel<SettingsState>() {

    private var cityToDelete : City? = null
    private val query = MutableStateFlow("")

    init {
        observeQuery()
        observeCities()
    }

    override fun createInitialState(): SettingsState = SettingsState.Loading

    override suspend fun handleEvent(event: UiEvent) {
        when (event) {
            is SettingsEvent.Query -> query.value = event.cityName
            is SettingsEvent.OnCityClick -> handleCityClick(event = event)
            is SettingsEvent.OnCloseSearch -> onCloseSearch()
            is SettingsEvent.OnCityCardClick -> handleCityCardClick(event = event)
            is SettingsEvent.CancelDeleteCity -> onCancelDeleteCity()
            is SettingsEvent.DeleteCityCard -> onDeleteCity(event = event)
            is SettingsEvent.CityDeleteConfirmed -> deleteCityFromDatabase()
        }
    }

    private fun observeQuery() = safeIoLaunch {
        query.debounce(500)
            .distinctUntilChanged()
            .collect {
                if (it.isEmpty()) {
                    val cities = cityRepository.getCities()
                    postState(SettingsState.Data(cities = cities))
                } else {
                    updateCities(it)
                }
            }
    }

    private fun observeCities() = safeIoLaunch {
        val cities = cityRepository.getCities()
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
        postEffect(SettingEffect.ClearSearchView)
        addNewCity(cityUi = event.cityUi)
        val cities = cityRepository.getCities()
        postState(SettingsState.Data(cities = cities))
    }

    private suspend fun handleCityCardClick(event: SettingsEvent.OnCityCardClick) {
        weatherListPreference.setPosition(event.position)
        postState(SettingsState.ShowWeatherList)
    }

    private suspend fun addNewCity(cityUi: CityUi) {
        weatherRepository.updateWeatherForAdditionalCity(
            lat = cityUi.lat,
            lon = cityUi.lon,
            timeZone = cityUi.timeZone,
            id = cityUi.id
        )
    }

    private suspend fun onCloseSearch() {
        val cities = cityRepository.getCities()
        postState(SettingsState.Data(cities = cities))
    }

    private suspend fun onCancelDeleteCity() {
        val cities = cityRepository.getCities()
        postState(SettingsState.Data(cities = cities))
    }

    private suspend fun deleteCityFromDatabase() {
        val cityToDelete = cityToDelete ?: return
        cityRepository.deleteCity(city = cityToDelete)
        val newCities = cityRepository.getCities()
        postState(SettingsState.Data(cities = newCities))
    }

    private suspend fun onDeleteCity(event: SettingsEvent.DeleteCityCard) {
        val cities = cityRepository.getCities()
        cityToDelete = cities[event.position]
        val newCities = cityRepository.getCities().filter { it != cityToDelete }
        postEffect(SettingEffect.ConfirmCityDelete)
        postState(SettingsState.Data(cities = newCities))
    }
}