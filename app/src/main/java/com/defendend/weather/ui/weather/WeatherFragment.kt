package com.defendend.weather.ui.weather

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.defendend.weather.R
import com.defendend.weather.databinding.FragmentWeatherBinding
import com.defendend.weather.models.weather.Daily
import com.defendend.weather.models.weather.Hourly
import com.defendend.weather.ui.base.WeatherState
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class WeatherFragment : Fragment(R.layout.fragment_weather) {

    private val binding: FragmentWeatherBinding by viewBinding(
        FragmentWeatherBinding::bind
    )

    private val viewModel: WeatherViewModel by viewModels()

    private var adapterHourly: WeatherHourlyAdapter? = WeatherHourlyAdapter(emptyList())
    private var adapterDaily: WeatherDailyAdapter? = WeatherDailyAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenResumed {
            viewModel.state.collect(::handleState)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            hourlyRecyclerView.layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            dailyRecyclerView.layoutManager = LinearLayoutManager(context)
            hourlyRecyclerView.adapter = adapterHourly
            dailyRecyclerView.adapter = adapterDaily
        }
    }

    private fun handleState(state: WeatherState) {
        when (state) {
            is WeatherState.Loading -> showLoading()
            is WeatherState.Data -> showData(state)
        }
    }

    private fun updateHourly(hourly: List<Hourly>) {
        adapterHourly = WeatherHourlyAdapter(hourly)
        binding.hourlyRecyclerView.adapter = adapterHourly
    }

    private fun updateDaily(daily: List<Daily>) {
        adapterDaily = WeatherDailyAdapter(daily)
        binding.dailyRecyclerView.adapter = adapterDaily
    }

    private fun showLoading() {
        binding.apply {
            cityName.text = getString(R.string.load_text)
            currentTemperature.text = getString(R.string.load_text)
            descriptionTextView.text = getString(R.string.load_text)
            maxMinTemperature.text = getString(R.string.min_max_temperature_load)
            hourlyTempDescription.text = getString(R.string.tomorrow_info_grow_load)
            precipitationDescriptionText.visibility = View.INVISIBLE
            windGustDescription.visibility = View.INVISIBLE
        }

    }

    private fun showData(data: WeatherState.Data) {
        val (isTempNotChanged, isTempGrow, maxTemp) = data.tomorrowInfo

        val hourlyDescription = if (isTempNotChanged) {
            getString(R.string.tomorrow_info_not_changed, maxTemp)
        } else {
            if (isTempGrow) {
                getString(R.string.tomorrow_info_grow_true, maxTemp)
            } else {
                getString(R.string.tomorrow_info_grow_false, maxTemp)
            }
        }

        updateHourly(data.hourly)
        updateDaily(data.daily)

        setSunTimes(
            sunrise = data.sunrise,
            sunset = data.sunset
        )

        val (descriptionRes, uviLevelRes) = data.uvIndexDescription

        val uviLevel = getString(uviLevelRes).lowercase()
        val descriptionUvi = getString(descriptionRes, uviLevel)

        binding.apply {
            cityName.text = data.currentCity
            currentTemperature.text = getString(R.string.temperature_rec, data.currentTemperature)
            descriptionTextView.text = data.description
            maxMinTemperature.text =
                getString(R.string.min_max_temperature, data.maxTemp, data.minTemp)
            hourlyTempDescription.text = getString(R.string.tomorrow_info_grow_load)
            precipitationDescriptionText.visibility = View.VISIBLE
            windGustDescription.visibility = View.VISIBLE
            hourlyTempDescription.text = hourlyDescription
            uviTextView.text = data.uvIndex
            uviLevelTextView.text = getString(data.uvIndexLevel)
            uviDescriptionTextView.text = descriptionUvi

            windSpeedTextView.text = getString(R.string.wind_speed, data.windSpeed)
            windGustTextView.text = getString(R.string.wind_gust_value, data.windGust)
            windDirectionTextView.text = getString(data.windDirection)
            precipitationMmTextView.text = getString(R.string.precipitation_mm, data.precipitation)
            feelsTemp.text = getString(R.string.temperature_rec, data.feelsLike)
            feelsLikeDescription.text = getString(data.feelsLikeDescription)
            pressureMmDescription.text = getString(R.string.pressure_mm, data.pressureMm)
            currentHumidityTextView.text =
                getString(R.string.humidity_text_card_view, data.humidity)
            dewPointTextView.text = getString(R.string.dew_point_text, data.dewPoint)
            visibilityDistance.text = getString(R.string.visibility_distance, data.visibility)
        }
    }

    private fun setSunTimes(sunrise: Long, sunset: Long) {
        val dateFormat: DateFormat = SimpleDateFormat("H:mm")
        dateFormat.timeZone = TimeZone.getDefault()

        val sunriseTime = dateFormat.format(Date(sunrise))
        val sunsetTime = dateFormat.format(Date(sunset))

        binding.apply {
            sunriseTextView.text = sunriseTime
            sunsetTextView.text = getString(R.string.sunset_text, sunsetTime)
        }

    }

    companion object {
        fun newInstance(): WeatherFragment {
            return WeatherFragment()
        }
    }
}