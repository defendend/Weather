package com.defendend.weather.ui.weather_list

import com.defendend.weather.ui.base.UiEffect
import com.defendend.weather.ui.base.UiEvent
import com.defendend.weather.ui.base.UiState

sealed class WeatherListEvent : UiEvent {
    object AddNewCity : WeatherListEvent()
    object DeleteCity : WeatherListEvent()
}

sealed class WeatherListState : UiState {
    object Loading : WeatherListState()
    data class Data(
        val cityNameList : List<String>
    ) : WeatherListState()
}

sealed class WeatherListEffect : UiEffect {
    object BadConnect : WeatherListEffect()
}

