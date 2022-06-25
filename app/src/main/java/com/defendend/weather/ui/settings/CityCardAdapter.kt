package com.defendend.weather.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.defendend.weather.R
import com.defendend.weather.ui.weather_list.City

class CityCardAdapter : ListAdapter<City, CityCardHolder>(DiffCallbackCityCard()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityCardHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val view = layoutInflater.inflate(R.layout.list_item_card_weather, parent, false)

        return CityCardHolder(view = view)
    }

    override fun onBindViewHolder(holder: CityCardHolder, position: Int) {
        val city = getItem(position)
        holder.bind(city = city)
    }

}

private class DiffCallbackCityCard : DiffUtil.ItemCallback<City>() {
    override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
        return (oldItem.name == newItem.name && oldItem.temperature == newItem.temperature)
    }

    override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
        return oldItem == newItem
    }
}