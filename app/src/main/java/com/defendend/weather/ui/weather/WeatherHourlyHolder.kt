package com.defendend.weather.ui.weather

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.defendend.weather.R
import com.defendend.weather.databinding.ListItemHourlyWeatherBinding
import com.defendend.weather.models.weather.Hourly
import com.defendend.weather.models.weather.WeatherXX
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class WeatherHourlyHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val context = view.context
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
                tempTextView.text = context.getString(
                    R.string.temp_text_horizontal_recycler,
                    temperature
                )

                weatherHourTextView.text = hour

                setWeatherIcons(weathers)
            }

        } else {
            setWeatherIcons(weathers)
            bindingHourly.apply {
                tempTextView.text = context.getString(
                    R.string.temp_text_horizontal_recycler,
                    temperature
                )
                weatherHourTextView.text = context.getString(R.string.item_temp_now)
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
                            context,
                            R.drawable.ic_clear_sky
                        )
                    )
                    "01n" -> weatherIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_moon
                        )
                    )
                    "02d" -> weatherIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_few_clouds
                        )
                    )
                    "02n" -> weatherIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_moon_clouds
                        )
                    )
                    "03d", "03n" -> weatherIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_clouds
                        )
                    )
                    "04d", "04n" -> weatherIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_broken_clouds
                        )
                    )
                    "09d", "09n" -> weatherIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_snower_rain
                        )
                    )
                    "10d", "10n" -> weatherIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_rain
                        )
                    )
                    "11d", "11n" -> weatherIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_thunderstorm
                        )
                    )
                    "13d", "13n" -> weatherIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_snow
                        )
                    )
                }
            }
        }
    }
}