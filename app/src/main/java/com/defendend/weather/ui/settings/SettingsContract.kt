package com.defendend.weather.ui.settings

import com.defendend.weather.models.city.CityUi
import com.defendend.weather.ui.base.UiEffect
import com.defendend.weather.ui.base.UiEvent
import com.defendend.weather.ui.base.UiState
import com.defendend.weather.ui.weather_list.City

sealed class SettingsEvent : UiEvent {
    data class Query(
        val cityName: String
    ) : SettingsEvent()

    data class DeleteCityCard(
        val position: Int
    ) : SettingsEvent()

    object CancelDeleteCity : SettingsEvent()
    object CityDeleteConfirmed : SettingsEvent()

    data class OnCityClick(
        val cityUi: CityUi
    ) : SettingsEvent()

    data class OnCityCardClick(
        val position: Int
    ) : SettingsEvent()

    object OnCloseSearch : SettingsEvent()
}

sealed class SettingsState : UiState {
    object Loading : SettingsState()
    object ShowWeatherList : SettingsState()
    data class SearchCity(
        val cityNames: List<CityUi>
    ) : SettingsState()

    data class Data(
        val cities: List<City>
    ) : SettingsState()
}

sealed class SettingEffect : UiEffect {
    object ConfirmCityDelete : SettingEffect()
    object ClearSearchView : SettingEffect()
}