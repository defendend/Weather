package com.defendend.weather.ui.settings

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.defendend.weather.R
import com.defendend.weather.databinding.ListItemCardWeatherBinding
import com.defendend.weather.ui.weather_list.City

private const val DEFAULT_CITY = "default"

class CityCardHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val context = view.context
    private val binding: ListItemCardWeatherBinding by viewBinding(
        ListItemCardWeatherBinding::bind
    )

    fun bind(city: City) {
        binding.apply {
            if (city.name == DEFAULT_CITY) {
                cityName.text = context.getString(R.string.current_location)
                localTime.text = city.cityName
                weatherInfoText.text = city.weatherInfo
                currentTemperature.text =
                    context.getString(R.string.temperature_rec, city.temperature)
                maxMinTemperature.text =
                    context.getString(R.string.min_max_temperature, city.maxTemp, city.minTemp)
            }
        }
    }
}