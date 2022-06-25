package com.defendend.weather.ui.settings

import com.defendend.weather.models.city.CityUi
import com.defendend.weather.ui.base.UiEvent
import com.defendend.weather.ui.base.UiState
import com.defendend.weather.ui.weather_list.City

sealed class SettingsEvent : UiEvent {
    data class Query(
        val cityName: String
    ) : SettingsEvent()

    data class OnCityClick(
        val id: Int
    ) : SettingsEvent()
    object OnCloseSearch : SettingsEvent()
}

sealed class SettingsState : UiState {
    object Loading : SettingsState()
    data class SearchCity(
        val cityNames: List<CityUi>
    ) : SettingsState()
    data class Data(
        val cities: List<City>
    ) : SettingsState()
}