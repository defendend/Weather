package com.defendend.weather.ui.weather_list

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.defendend.weather.models.city.CityUi
import com.defendend.weather.ui.weather.city.CityWeatherFragment
import com.defendend.weather.ui.weather.current_location.CurrentLocationWeatherFragment

class WeatherPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private var citiesList: List<CityUi> = emptyList()

    override fun getItemCount(): Int = citiesList.size + 1

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CurrentLocationWeatherFragment.newInstance()
            else -> CityWeatherFragment.newInstance(citiesList[position-1])
        }
    }

    fun updateCities(cityIdList: List<CityUi>) {
        citiesList = cityIdList
        notifyDataSetChanged()
    }

}