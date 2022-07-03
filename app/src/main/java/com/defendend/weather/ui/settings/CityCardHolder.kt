package com.defendend.weather.ui.settings


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.defendend.weather.R
import com.defendend.weather.api.WeatherApi.Companion.TAG_RU
import com.defendend.weather.databinding.ListItemCardWeatherBinding
import com.defendend.weather.ui.weather_list.City
import java.text.SimpleDateFormat
import java.util.*

private const val DEFAULT_CITY = "default"

class CityCardHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val context = view.context
    private val binding: ListItemCardWeatherBinding by viewBinding(
        ListItemCardWeatherBinding::bind
    )

    fun bind(city: City, onClick: (Int) -> Unit) {
        val localTag: String = Locale.getDefault().toLanguageTag().take(2)

        binding.apply {
            if (city.id == DEFAULT_CITY) {
                if (localTag == TAG_RU) {
                    localTime.text = city.cityName
                } else {
                    localTime.text = city.cityNameEn
                }
                cityName.text = context.getString(R.string.current_location)
                weatherInfoText.text = city.weatherInfo
                currentTemperature.text =
                    context.getString(R.string.temperature_rec, city.temperature)
                maxMinTemperature.text =
                    context.getString(R.string.min_max_temperature, city.maxTemp, city.minTemp)
            } else {
                val sdf = SimpleDateFormat("H:mm")
                val time = Calendar.getInstance().time
                sdf.timeZone = TimeZone.getTimeZone(city.timeZone)
                localTime.text = sdf.format(time)
                if (localTag == TAG_RU) {
                    cityName.text = city.cityName
                } else {
                    cityName.text = city.cityNameEn
                }
                weatherInfoText.text = city.weatherInfo
                currentTemperature.text =
                    context.getString(R.string.temperature_rec, city.temperature)
                maxMinTemperature.text =
                    context.getString(R.string.min_max_temperature, city.maxTemp, city.minTemp)
            }
        }

        binding.root.setOnClickListener {
            onClick(absoluteAdapterPosition)
        }
    }
}