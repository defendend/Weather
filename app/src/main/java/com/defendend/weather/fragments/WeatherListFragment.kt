package com.defendend.weather.fragments

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.defendend.weather.R
import com.defendend.weather.WeatherCity
import com.defendend.weather.viewmodels.WeatherListViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class WeatherListFragment : Fragment() {

    private lateinit var weatherRecyclerView: RecyclerView
    private var adapter: WeatherAdapter? = WeatherAdapter(emptyList())

    private val weatherListViewModel: WeatherListViewModel by lazy {
        ViewModelProviders.of(this).get(WeatherListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_weather_list, container, false)

        view.apply {
            weatherRecyclerView = findViewById(R.id.weatherRecyclerView)
        }

        weatherRecyclerView.layoutManager = LinearLayoutManager(context)

        weatherRecyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherListViewModel.weatherListLiveData.observe(
            viewLifecycleOwner,
            Observer { weatherCites ->
                weatherCites?.let {
                    updateUI(weatherCites)
                }
            }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_weather_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.newWeaherCity -> {
                val weatherCity = WeatherCity(
                    name = "Великий новгород",
                    temperature = (Math.random()*15).toInt() - 10,
                    geolocation = false)
                weatherListViewModel.addWeatherCity(weatherCity)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI(weatherCities: List<WeatherCity>) {
        adapter = WeatherAdapter(weatherCities)
        weatherRecyclerView.adapter = adapter

    }

    private inner class WeatherHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var weatherCity: WeatherCity

        private val currentTimeTextView: TextView = itemView.findViewById(R.id.currentTimeTextView)
        private val cityTextView: TextView = itemView.findViewById(R.id.cityTextView)
        private val tempTextView: TextView = itemView.findViewById(R.id.tempTextView)
        private val geolocationImageView: ImageView =
            itemView.findViewById(R.id.geolocationImageView)

        fun bind(weatherCity: WeatherCity) {
            this.weatherCity = weatherCity
            cityTextView.text = weatherCity.name
            tempTextView.text = getString(R.string.temperature_rec, weatherCity.temperature)
            if (!weatherCity.geolocation) {
                geolocationImageView.visibility = View.GONE
            } else {
                currentTimeTextView.visibility = View.INVISIBLE
            }
            val dateFormat: DateFormat = SimpleDateFormat("H:mm")
            currentTimeTextView.text = dateFormat.format(Date())
        }

    }

    private inner class WeatherAdapter(var weatherCites: List<WeatherCity>) :
        ListAdapter<WeatherCity, WeatherHolder>(DiffCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherHolder {
            val view = layoutInflater.inflate(R.layout.list_item_weather_sun, parent, false)

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