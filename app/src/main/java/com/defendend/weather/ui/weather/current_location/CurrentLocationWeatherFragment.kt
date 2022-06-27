package com.defendend.weather.ui.weather.current_location

import androidx.fragment.app.viewModels
import com.defendend.weather.ui.weather.base.BaseWeatherFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CurrentLocationWeatherFragment : BaseWeatherFragment() {

    override val viewModel: CurrentLocationViewModel by viewModels()

    companion object {
        fun newInstance(): CurrentLocationWeatherFragment {
            return CurrentLocationWeatherFragment()
        }
    }
}