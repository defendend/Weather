package com.defendend.weather.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.defendend.weather.R
import com.defendend.weather.models.city.CityUi

class CityNameSearchAdapter(
    private val onClick: (CityUi) -> Unit
) : ListAdapter<CityUi, SettingsNameHolder>(DiffCallbackName()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsNameHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val view = layoutInflater.inflate(R.layout.search_city_card, parent, false)

        return SettingsNameHolder(view = view)
    }

    override fun onBindViewHolder(holder: SettingsNameHolder, position: Int) {
        val cityName = getItem(position)
        holder.bind(cityName, onClick)
    }
}


private class DiffCallbackName : DiffUtil.ItemCallback<CityUi>() {
    override fun areItemsTheSame(oldItem: CityUi, newItem: CityUi): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CityUi, newItem: CityUi): Boolean {
        return oldItem == newItem
    }


}