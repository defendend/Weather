package com.defendend.weather.ui.weather.city

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.defendend.weather.models.city.CityUi
import com.defendend.weather.ui.weather.base.BaseWeatherFragment
import com.defendend.weather.ui.weather.base.WeatherEvent
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalArgumentException

@AndroidEntryPoint
class CityWeatherFragment : BaseWeatherFragment() {

    override val viewModel: CityWeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val city = arguments?.getParcelable<CityUi>(CITY)
        if (city != null) {
            val event = WeatherEvent.CityParameter(city = city)
            viewModel.postEvent(event = event)
        } else {
            throw IllegalArgumentException("City name, lat or lon parameter is null.")
        }
    }

    companion object {
        private const val CITY = "city"

        fun newInstance(cityUi: CityUi): CityWeatherFragment {
            return CityWeatherFragment().apply {
                arguments = bundleOf(CITY to cityUi)
            }
        }
    }
}