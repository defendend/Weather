package com.defendend.weather.ui.weather.city

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.defendend.weather.ui.weather.base.BaseWeatherFragment
import com.defendend.weather.ui.weather.base.WeatherEvent
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalArgumentException

@AndroidEntryPoint
class CityWeatherFragment : BaseWeatherFragment() {

    override val viewModel: CityWeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cityName = arguments?.getString(CITY_NAME_ARG)
        if (cityName != null) {
            val event = WeatherEvent.CityParameter(name = cityName)
            viewModel.postEvent(event = event)
        } else {
            throw IllegalArgumentException("City name parameter is null.")
        }
    }

    companion object {
        private const val CITY_NAME_ARG = "cityNameArg"

        fun newInstance(cityName: String) : CityWeatherFragment{
            return CityWeatherFragment().apply {
                arguments = bundleOf(CITY_NAME_ARG to cityName)
            }
        }
    }
}