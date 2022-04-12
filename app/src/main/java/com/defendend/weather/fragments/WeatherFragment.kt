package com.defendend.weather.fragments

import android.os.Bundle
import android.view.LayoutInflater
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
import com.defendend.weather.R
import com.defendend.weather.features.weather.Daily
import com.defendend.weather.features.weather.Hourly
import com.defendend.weather.features.weather.WeatherX
import com.defendend.weather.features.weather.WeatherXX
import com.defendend.weather.ui.base.WeatherState
import com.defendend.weather.ui.base.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@AndroidEntryPoint
class WeatherFragment : Fragment() {

    private lateinit var weatherHourlyRecyclerView: RecyclerView
    private lateinit var weatherDailyRecyclerView: RecyclerView

    private val cityName: TextView?
        get() = view?.findViewById(R.id.city_name)

    private val currentTemperature: TextView?
        get() = view?.findViewById(R.id.current_temp)

    private val descriptionTextView: TextView?
        get() = view?.findViewById(R.id.description_text_view)

    private val minMaxTemperature: TextView?
        get() = view?.findViewById(R.id.max_min_temp)

    private val tomorrowDescriptionTextView: TextView?
        get() = view?.findViewById(R.id.temp_description)

    private val uvIndexTextView: TextView?
        get() = view?.findViewById(R.id.uvi_text_view)

    private val uvIndexLevelTextView: TextView?
        get() = view?.findViewById(R.id.uvi_level_text_view)

    private val uvIndexDescriptionTextView: TextView?
        get() = view?.findViewById(R.id.uvi_description_text_view)

    private val sunriseTextView: TextView?
        get() = view?.findViewById(R.id.sunrise_text_view)

    private val sunsetTextView: TextView?
        get() = view?.findViewById(R.id.sunset_text_view)

    private val windSpeedTextView: TextView?
        get() = view?.findViewById(R.id.wind_speed_text_view)

    private val windGustTextView: TextView?
        get() = view?.findViewById(R.id.wind_gust_text_view)

    private val windDirectionTextView: TextView?
        get() = view?.findViewById(R.id.wind_direction_text_view)

    private val viewModel: WeatherViewModel by viewModels()

    private var adapterHourly: WeatherHourlyAdapter? = WeatherHourlyAdapter(emptyList())
    private var adapterDaily: WeatherDailyAdapter? = WeatherDailyAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenResumed {
            viewModel.state.collect(::handleState)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weather, container, false)

        view.apply {
            weatherHourlyRecyclerView = findViewById(R.id.recycler_horizontal_view)
            weatherDailyRecyclerView = findViewById(R.id.daily_recycler_view)
        }

        weatherHourlyRecyclerView.layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }

        weatherDailyRecyclerView.layoutManager = LinearLayoutManager(context)

        weatherHourlyRecyclerView.adapter = adapterHourly
        weatherDailyRecyclerView.adapter = adapterDaily
        return view
    }

    private fun handleState(state: WeatherState) {
        when (state) {
            is WeatherState.Loading -> showLoading()
            is WeatherState.Data -> showData(state)
        }
    }

    private fun updateHourly(hourly: List<Hourly>) {
        adapterHourly = WeatherHourlyAdapter(hourly)
        weatherHourlyRecyclerView.adapter = adapterHourly
    }

    private fun updateDaily(daily: List<Daily>) {
        adapterDaily = WeatherDailyAdapter(daily)
        weatherDailyRecyclerView.adapter = adapterDaily
    }

    private fun showLoading() {
        cityName?.text = getString(R.string.load_text)
        currentTemperature?.text = getString(R.string.load_text)
        descriptionTextView?.text = getString(R.string.load_text)
        minMaxTemperature?.text = getString(R.string.min_max_temperature_load)
        tomorrowDescriptionTextView?.text = getString(R.string.tomorrow_info_grow_load)

    }

    private fun showData(data: WeatherState.Data) {
        cityName?.text = data.currentCity
        currentTemperature?.text = data.currentTemperature
        descriptionTextView?.text = data.description
        minMaxTemperature?.text =
            getString(R.string.min_max_temperature, data.maxTemp, data.minTemp)

        val (tempInfo, maxTemp) = data.tomorrowInfo
        val (isTempNotChanged, isTempGrow) = tempInfo

        tomorrowDescriptionTextView?.text = if (isTempNotChanged) {
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

        uvIndexTextView?.text = data.uvIndex
        uvIndexLevelTextView?.text = data.uvIndexLevel
        uvIndexDescriptionTextView?.text = data.uvIndexDescription

        setSunTimes(
            sunrise = data.sunrise,
            sunset = data.sunset
        )

        windSpeedTextView?.text = getString(R.string.wind_speed, data.windSpeed)
        windGustTextView?.text = getString(R.string.wind_gust_value, data.windGust)
        windDirectionTextView?.text = data.windDirection
    }

    private fun setSunTimes(sunrise: Long, sunset: Long) {
        val dateFormat: DateFormat = SimpleDateFormat("H:mm")
        dateFormat.timeZone = TimeZone.getDefault()

        val sunriseTime = dateFormat.format(Date(sunrise))
        val sunsetTime = dateFormat.format(Date(sunset))

        sunriseTextView?.text = sunriseTime
        sunsetTextView?.text = getString(R.string.sunset_text, sunsetTime)
    }

    private inner class WeatherDailyHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var daily: Daily

        private val dayOfWeekTextView: TextView = itemView.findViewById(R.id.day_of_week_text_view)
        private val minTempTextView: TextView = itemView.findViewById(R.id.min_temp)
        private val maxTempTextView: TextView = itemView.findViewById(R.id.max_temp)
        private val humidityTextView: TextView = itemView.findViewById(R.id.humidity_text_view)
        private val weatherIcon: ImageView = itemView.findViewById(R.id.weather_daily_icon)
        private val viewLine: View = itemView.findViewById(R.id.view)

        fun bind(daily: Daily) {
            this.daily = daily

            if (adapterPosition != 0) {

                val timestamp = (this.daily.dt)?.times(1000) ?: 0

                val dateFormat: DateFormat = SimpleDateFormat("E")
                dateFormat.timeZone = TimeZone.getDefault()

                val day = dateFormat.format(Date(timestamp))

                dayOfWeekTextView.text = day

                val humidity = this.daily.humidity ?: 0

                humidityTextView.text = getString(R.string.humidity_text, humidity)

                minTempTextView.text = getString(
                    R.string.temp_text_horizontal_recycler,
                    this.daily.temp?.min?.roundToInt().toString()
                )
                maxTempTextView.text = getString(
                    R.string.temp_text_horizontal_recycler,
                    this.daily.temp?.max?.roundToInt().toString()
                )

                setWeatherIcons(daily.weather.orEmpty())
            } else {
                val minTemp = this.daily.temp?.min?.roundToInt().toString()
                val maxTemp = this.daily.temp?.max?.roundToInt().toString()
                val humidity = this.daily.humidity ?: 0

                humidityTextView.text = getString(R.string.humidity_text, humidity)

                dayOfWeekTextView.text = "Сегодня"

                minTempTextView.text = getString(
                    R.string.temp_text_horizontal_recycler,
                    minTemp
                )

                maxTempTextView.text = getString(
                    R.string.temp_text_horizontal_recycler,
                    maxTemp
                )

                setWeatherIcons(daily.weather.orEmpty())
            }

            val count = adapterDaily?.itemCount ?: 0

            if (adapterPosition == count - 1) {
                viewLine.visibility = View.GONE
            }
        }

        private fun setWeatherIcons(weather: List<WeatherX>) {
            val hourWeather = weather.firstOrNull()
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

    private inner class WeatherHourlyHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var hourly: Hourly

        private val hourTextView: TextView = itemView.findViewById(R.id.weather_hour_text_view)
        private val tempTextView: TextView = itemView.findViewById(R.id.temp_text_view)
        private val weatherIcon: ImageView = itemView.findViewById(R.id.weather_icon)

        fun bind(hourly: Hourly) {
            this.hourly = hourly

            if (adapterPosition != 0) {

                tempTextView.text = getString(
                    R.string.temp_text_horizontal_recycler,
                    this.hourly.temp?.roundToInt().toString()
                )


                val timeStamp = (this.hourly.dt)?.times(1000L) ?: 0

                val dateFormat: DateFormat = SimpleDateFormat("HH")
                dateFormat.timeZone = TimeZone.getDefault()

                val hour = dateFormat.format(Date(timeStamp))

                hourTextView.text = hour

                setWeatherIcons(hourly.weather.orEmpty())
            } else {
                setWeatherIcons(hourly.weather.orEmpty())
                tempTextView.text = getString(
                    R.string.temp_text_horizontal_recycler,
                    this.hourly.temp?.roundToInt().toString()
                )
            }
        }

        private fun setWeatherIcons(weather: List<WeatherXX>) {
            val hourWeather = weather.firstOrNull()
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

    private inner class WeatherDailyAdapter(var daily: List<Daily>) :
        ListAdapter<Daily, WeatherDailyHolder>(DiffCallbackDaily()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherDailyHolder {
            val view = layoutInflater.inflate(R.layout.item_list_daily_weather, parent, false)

            return WeatherDailyHolder(view = view)
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

            val layout = if (viewType == 0) {
                R.layout.list_item_current_weather
            } else {
                R.layout.list_item_hourly_weather
            }
            val view = layoutInflater.inflate(layout, parent, false)

            return WeatherHourlyHolder(view = view)
        }

        override fun onBindViewHolder(holder: WeatherHourlyHolder, position: Int) {
            val hourlyWeather = hourly[position]
            holder.bind(hourlyWeather)
        }

        override fun getItemCount(): Int = hourly.size

        override fun getItemViewType(position: Int): Int = if (position == 0) 0 else 1

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