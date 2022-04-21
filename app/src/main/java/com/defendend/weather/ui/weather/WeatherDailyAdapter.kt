package com.defendend.weather.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.defendend.weather.R
import com.defendend.weather.models.weather.Daily

class WeatherDailyAdapter(private val daily: List<Daily>) :
    ListAdapter<Daily, WeatherDailyHolder>(DiffCallbackDaily()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherDailyHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_list_daily_weather, parent, false)

        return WeatherDailyHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherDailyHolder, position: Int) {
        val day = daily[position]
        holder.bind(daily = day)
    }

    override fun getItemCount(): Int = daily.size
}


private class DiffCallbackDaily : DiffUtil.ItemCallback<Daily>() {
    override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem == newItem
    }

}