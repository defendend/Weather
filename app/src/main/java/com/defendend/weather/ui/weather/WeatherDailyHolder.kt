package com.defendend.weather.ui.weather

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.defendend.weather.R
import com.defendend.weather.databinding.ItemListDailyWeatherBinding
import com.defendend.weather.models.weather.Daily
import com.defendend.weather.models.weather.WeatherX
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class WeatherDailyHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val context = view.context
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

                humidityTextView.text = context.getString(R.string.humidity_text, humidity)

                minTemp.text = context.getString(
                    R.string.temp_text_horizontal_recycler,
                    minTemperature
                )
                maxTemp.text =
                    context.getString(R.string.temp_text_horizontal_recycler, maxTemperature)

                setWeatherIcons(weathers)
            }

        } else {
            bindingDaily.apply {
                humidityTextView.text = context.getString(R.string.humidity_text, humidity)

                bindingDaily.dayOfWeekTextView.text = context.getString(R.string.today_text_daily)

                minTemp.text =
                    context.getString(R.string.temp_text_horizontal_recycler, minTemperature)

                maxTemp.text =
                    context.getString(R.string.temp_text_horizontal_recycler, maxTemperature)

                setWeatherIcons(weathers)
            }

        }

        val count = bindingAdapter?.itemCount ?: 0

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
                            context,
                            R.drawable.ic_clear_sky
                        )
                    )
                    "01n" -> weatherDailyIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_moon
                        )
                    )
                    "02d" -> weatherDailyIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_few_clouds
                        )
                    )
                    "02n" -> weatherDailyIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_moon_clouds
                        )
                    )
                    "03d", "03n" -> weatherDailyIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_clouds
                        )
                    )
                    "04d", "04n" -> weatherDailyIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_broken_clouds
                        )
                    )
                    "09d", "09n" -> weatherDailyIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_snower_rain
                        )
                    )
                    "10d", "10n" -> weatherDailyIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_rain
                        )
                    )
                    "11d", "11n" -> weatherDailyIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_thunderstorm
                        )
                    )
                    "13d", "13n" -> weatherDailyIcon.setImageDrawable(
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