package com.defendend.weather.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.defendend.weather.R
import com.defendend.weather.models.weather.Hourly

class WeatherHourlyAdapter(var hourly: List<Hourly>) :
    ListAdapter<Hourly, WeatherHourlyHolder>(DiffCallbackHourly()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherHourlyHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_hourly_weather, parent, false)

        return WeatherHourlyHolder(view = view)
    }

    override fun onBindViewHolder(holder: WeatherHourlyHolder, position: Int) {
        val hourlyWeather = hourly[position]
        holder.bind(hourlyWeather)
    }

    override fun getItemCount(): Int = hourly.size

}

private class DiffCallbackHourly : DiffUtil.ItemCallback<Hourly>() {
    override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        if (oldItem != newItem) return false

        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem == newItem
    }
}