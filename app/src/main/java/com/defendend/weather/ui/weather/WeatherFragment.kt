package com.defendend.weather.ui.weather

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.defendend.weather.R
import com.defendend.weather.databinding.FragmentWeatherBinding
import com.defendend.weather.models.weather.Daily
import com.defendend.weather.models.weather.Hourly
import com.defendend.weather.models.weather.TileItem
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WeatherFragment : Fragment(R.layout.fragment_weather) {

    private val binding: FragmentWeatherBinding by viewBinding(
        FragmentWeatherBinding::bind
    )

    private val viewModel: WeatherViewModel by viewModels()

    private var adapterHourly: WeatherHourlyAdapter = WeatherHourlyAdapter(emptyList())
    private var adapterDaily: WeatherDailyAdapter = WeatherDailyAdapter(emptyList())
    private var adapterCardItems: WeatherCardItemAdapter = WeatherCardItemAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenResumed {
            viewModel.state.collect(::handleState)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            hourlyRecyclerView.layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            hourlyRecyclerView.adapter = adapterHourly

            dailyRecyclerView.layoutManager = LinearLayoutManager(context)
            dailyRecyclerView.adapter = adapterDaily
            val dividerItemDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
            dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.divider_drawable))
            dailyRecyclerView.addItemDecoration(dividerItemDecoration)

            cardItemRecyclerView.apply {
                layoutManager = GridLayoutManager(context, 2)
                val spacingInPixels = resources.getDimensionPixelSize(R.dimen.normal_space)
                addItemDecoration(GridSpacingItemDecoration(2, spacingInPixels, 0))
                adapter = adapterCardItems
            }
        }
    }

    private fun handleState(state: WeatherState) {
        when (state) {
            is WeatherState.Loading -> showLoading()
            is WeatherState.Data -> showData(state)
        }
    }

    private fun updateHourly(hourly: List<Hourly>) {
        adapterHourly = WeatherHourlyAdapter(hourly)
        binding.hourlyRecyclerView.adapter = adapterHourly
    }

    private fun updateDaily(daily: List<Daily>) {
        adapterDaily = WeatherDailyAdapter(daily)
        binding.dailyRecyclerView.adapter = adapterDaily
    }

    private fun updateCardItems(cardItems: List<TileItem>) {
        adapterCardItems = WeatherCardItemAdapter(cardItems)
        binding.cardItemRecyclerView.adapter = adapterCardItems
    }

    private fun showLoading() {
        binding.apply {
            cityName.text = getString(R.string.load_text)
            currentTemperature.text = getString(R.string.load_text)
            descriptionTextView.text = getString(R.string.load_text)
            maxMinTemperature.text = getString(R.string.min_max_temperature_load)
            hourlyTempDescription.text = getString(R.string.tomorrow_info_grow_load)
        }

    }

    private fun showData(data: WeatherState.Data) {
        updateHourly(data.hourly)
        updateDaily(data.daily)
        updateCardItems(data.cardItems)

        binding.apply {
            cityName.text = data.currentCity
            currentTemperature.text = getString(R.string.temperature_rec, data.currentTemperature)
            descriptionTextView.text = data.description
            maxMinTemperature.text =
                getString(R.string.min_max_temperature, data.maxTemp, data.minTemp)
            hourlyTempDescription.text = getString(R.string.tomorrow_info_grow_load)
        }
    }

    companion object {
        fun newInstance(): WeatherFragment {
            return WeatherFragment()
        }
    }
}