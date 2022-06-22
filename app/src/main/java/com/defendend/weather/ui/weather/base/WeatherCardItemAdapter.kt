package com.defendend.weather.ui.weather.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.defendend.weather.R
import com.defendend.weather.models.weather.TileItem

class WeatherCardItemAdapter(private val cardItems: List<TileItem>) :
    ListAdapter<TileItem, WeatherCardItemHolder>(DiffCallbackCardItems()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherCardItemHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val layout = when (viewType) {
            0 -> R.layout.list_item_current_weather
            1 -> R.layout.list_item_current_weather_more_info
            else -> R.layout.list_item_current_weather
        }

        val view = layoutInflater.inflate(layout, parent, false)

        return WeatherCardItemHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherCardItemHolder, position: Int) {
        val cardItem = cardItems[position]
        holder.bind(cardItem)
    }

    override fun getItemCount(): Int = cardItems.size

    override fun getItemViewType(position: Int): Int {
        return when (cardItems[position]) {
            is TileItem.BasicItem -> 0
            is TileItem.UvIndex -> 1
            is TileItem.Wind -> 1
        }
    }
}


private class DiffCallbackCardItems : DiffUtil.ItemCallback<TileItem>() {

    override fun areItemsTheSame(oldItem: TileItem, newItem: TileItem): Boolean {
        return oldItem.javaClass == newItem.javaClass
    }

    override fun areContentsTheSame(oldItem: TileItem, newItem: TileItem): Boolean {
        return oldItem == newItem
    }

}