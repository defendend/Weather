package com.defendend.weather.ui.weather_list

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.defendend.weather.ui.weather.WeatherFragment

class WeatherPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return WeatherFragment.newInstance()
    }
}