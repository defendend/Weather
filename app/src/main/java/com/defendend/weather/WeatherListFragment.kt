package com.defendend.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.measureTimedValue

class WeatherListFragment : Fragment() {

    private lateinit var weatherRecyclerView: RecyclerView
    private var adapter: WeatherAdapter? =WeatherAdapter(listOf(WeatherCity("Великий Новгород",5, true),
        WeatherCity("Санкт-Петербург",-1),
        WeatherCity("Москва", 7)
    ))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_weather, container, false)

        view.apply {
            weatherRecyclerView = findViewById(R.id.weatherRecyclerView)
        }

        weatherRecyclerView.layoutManager = LinearLayoutManager(context)

        weatherRecyclerView.adapter = adapter

        return view
    }

    private inner class WeatherHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var weatherCity: WeatherCity

        private val currentTimeTextView: TextView = itemView.findViewById(R.id.currentTimeTextView)
        private val cityTextView: TextView = itemView.findViewById(R.id.cityTextView)
        private val tempTextView: TextView = itemView.findViewById(R.id.tempTextView)
        private val geolocationImageView: ImageView = itemView.findViewById(R.id.geolocationImageView)

        fun bind(weatherCity: WeatherCity){
            this.weatherCity = weatherCity
            cityTextView.text = weatherCity.name
            tempTextView.text = getString(R.string.temperature_rec, weatherCity.temperature)
            if (!weatherCity.geolocation) {
                geolocationImageView.visibility = View.GONE
            }else {
                currentTimeTextView.visibility = View.INVISIBLE
            }
            val dateFormat: DateFormat = SimpleDateFormat("H:mm")
            currentTimeTextView.text = dateFormat.format(Date())
        }

    }

    private inner class WeatherAdapter(var weatherCites: List<WeatherCity>) :
        ListAdapter<WeatherCity,WeatherHolder>(DiffCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherHolder {
            val view =layoutInflater.inflate(R.layout.list_item_weather_sun, parent, false)

            return WeatherHolder(view)
        }

        override fun onBindViewHolder(holder: WeatherHolder, position: Int) {
            val weatherCity = weatherCites[position]
            holder.bind(weatherCity)
        }

        override fun getItemCount(): Int = weatherCites.size

    }

    private inner class DiffCallback : DiffUtil.ItemCallback<WeatherCity>() {
        override fun areItemsTheSame(oldItem: WeatherCity, newItem: WeatherCity): Boolean {
            if (oldItem != newItem) return false

            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: WeatherCity, newItem: WeatherCity): Boolean {
            return oldItem == newItem
        }

    }


    companion object {
        fun newInstance(): WeatherListFragment {
            return WeatherListFragment()
        }
    }
}