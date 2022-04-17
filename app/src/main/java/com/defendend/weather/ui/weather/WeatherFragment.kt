package com.defendend.weather.ui.weather

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.defendend.weather.R
import com.defendend.weather.databinding.FragmentWeatherBinding
import com.defendend.weather.databinding.ItemListDailyWeatherBinding
import com.defendend.weather.databinding.ListItemHourlyWeatherBinding
import com.defendend.weather.models.weather.Daily
import com.defendend.weather.models.weather.Hourly
import com.defendend.weather.models.weather.WeatherX
import com.defendend.weather.models.weather.WeatherXX
import com.defendend.weather.ui.base.WeatherState
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

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
        val (tempInfo, maxTemp) = data.tomorrowInfo
        val (isTempNotChanged, isTempGrow) = tempInfo

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

    private inner class WeatherDailyHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var daily: Daily

        private val bindingDaily: ItemListDailyWeatherBinding by viewBinding(
            ItemListDailyWeatherBinding::bind
        )

        fun bind(daily: Daily) {
            this.daily = daily

            val timestamp = (this.daily.dt)?.times(1000) ?: 0

            val dateFormat: DateFormat = SimpleDateFormat("E")
            dateFormat.timeZone = TimeZone.getDefault()

            val day = dateFormat.format(Date(timestamp))

            val minTemperature = this.daily.temp?.min?.roundToInt().toString()
            val maxTemperature = this.daily.temp?.max?.roundToInt().toString()
            val humidity = this.daily.humidity ?: 0

            val weathers = daily.weather.orEmpty()

            if (bindingAdapterPosition != 0) {

                bindingDaily.apply {
                    dayOfWeekTextView.text = day

                    humidityTextView.text = getString(R.string.humidity_text, humidity)

                    minTemp.text = getString(
                        R.string.temp_text_horizontal_recycler,
                        minTemperature
                    )
                    maxTemp.text = getString(R.string.temp_text_horizontal_recycler, maxTemperature)

                    setWeatherIcons(weathers)
                }

            } else {
                bindingDaily.apply {
                    humidityTextView.text = getString(R.string.humidity_text, humidity)

                    bindingDaily.dayOfWeekTextView.text = getString(R.string.today_text_daily)

                    minTemp.text = getString(R.string.temp_text_horizontal_recycler, minTemperature)

                    maxTemp.text = getString(R.string.temp_text_horizontal_recycler, maxTemperature)

                    setWeatherIcons(weathers)
                }

            }

            val count = adapterDaily?.itemCount ?: 0

            if (bindingAdapterPosition == count - 1) {
                bindingDaily.view.visibility = View.GONE
            }
        }

        private fun setWeatherIcons(weather: List<WeatherX>) {
            val hourWeather = weather.firstOrNull()
            bindingDaily.apply {
                if (hourWeather != null) {
                    when (hourWeather.icon) {
                        "01d" -> weatherDailyIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_clear_sky
                            )
                        )
                        "01n" -> weatherDailyIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_moon
                            )
                        )
                        "02d" -> weatherDailyIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_few_clouds
                            )
                        )
                        "02n" -> weatherDailyIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_moon_clouds
                            )
                        )
                        "03d", "03n" -> weatherDailyIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_clouds
                            )
                        )
                        "04d", "04n" -> weatherDailyIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_broken_clouds
                            )
                        )
                        "09d", "09n" -> weatherDailyIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_snower_rain
                            )
                        )
                        "10d", "10n" -> weatherDailyIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_rain
                            )
                        )
                        "11d", "11n" -> weatherDailyIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_thunderstorm
                            )
                        )
                        "13d", "13n" -> weatherDailyIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_snow
                            )
                        )
                    }
                }
            }
        }
    }

    private inner class WeatherHourlyHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var hourly: Hourly

        private val bindingHourly: ListItemHourlyWeatherBinding by viewBinding(
            ListItemHourlyWeatherBinding::bind
        )

        fun bind(hourly: Hourly) {
            this.hourly = hourly

            val temperature = this.hourly.temp?.roundToInt().toString()
            val timeStamp = (this.hourly.dt)?.times(1000L) ?: 0

            val dateFormat: DateFormat = SimpleDateFormat("HH")
            dateFormat.timeZone = TimeZone.getDefault()

            val hour = dateFormat.format(Date(timeStamp))

            val weathers = hourly.weather.orEmpty()

            if (bindingAdapterPosition != 0) {
                bindingHourly.apply {
                    tempTextView.text = getString(
                        R.string.temp_text_horizontal_recycler,
                        temperature
                    )

                    weatherHourTextView.text = hour

                    setWeatherIcons(weathers)
                }

            } else {
                setWeatherIcons(weathers)
                bindingHourly.apply {
                    tempTextView.text = getString(
                        R.string.temp_text_horizontal_recycler,
                        temperature
                    )
                    weatherHourTextView.text = getString(R.string.item_temp_now)
                }


            }
        }

        private fun setWeatherIcons(weather: List<WeatherXX>) {
            val hourWeather = weather.firstOrNull()

            bindingHourly.apply {
                if (hourWeather != null) {
                    when (hourWeather.icon) {
                        "01d" -> weatherIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_clear_sky
                            )
                        )
                        "01n" -> weatherIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_moon
                            )
                        )
                        "02d" -> weatherIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_few_clouds
                            )
                        )
                        "02n" -> weatherIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_moon_clouds
                            )
                        )
                        "03d", "03n" -> weatherIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_clouds
                            )
                        )
                        "04d", "04n" -> weatherIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_broken_clouds
                            )
                        )
                        "09d", "09n" -> weatherIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_snower_rain
                            )
                        )
                        "10d", "10n" -> weatherIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_rain
                            )
                        )
                        "11d", "11n" -> weatherIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_thunderstorm
                            )
                        )
                        "13d", "13n" -> weatherIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_snow
                            )
                        )
                    }
                }
            }
        }
    }

    private inner class WeatherDailyAdapter(var daily: List<Daily>) :
        ListAdapter<Daily, WeatherDailyHolder>(DiffCallbackDaily()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherDailyHolder {
            val view = layoutInflater.inflate(R.layout.item_list_daily_weather, parent, false)

            return WeatherDailyHolder(view)
        }

        override fun onBindViewHolder(holder: WeatherDailyHolder, position: Int) {
            val day = daily[position]
            holder.bind(daily = day)
        }

        override fun getItemCount(): Int = daily.size
    }

    private inner class WeatherHourlyAdapter(var hourly: List<Hourly>) :
        ListAdapter<Hourly, WeatherHourlyHolder>(DiffCallbackHourly()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherHourlyHolder {

            val view = layoutInflater.inflate(R.layout.list_item_hourly_weather, parent, false)

            return WeatherHourlyHolder(view = view)
        }

        override fun onBindViewHolder(holder: WeatherHourlyHolder, position: Int) {
            val hourlyWeather = hourly[position]
            holder.bind(hourlyWeather)
        }

        override fun getItemCount(): Int = hourly.size

    }

    private inner class DiffCallbackHourly : DiffUtil.ItemCallback<Hourly>() {
        override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
            if (oldItem != newItem) return false

            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
            return oldItem == newItem
        }
    }

    private inner class DiffCallbackDaily : DiffUtil.ItemCallback<Daily>() {
        override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
            return oldItem == newItem
        }

    }

    companion object {
        fun newInstance(): WeatherFragment {
            return WeatherFragment()
        }
    }
}