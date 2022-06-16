package com.defendend.weather.ui.weather_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.defendend.weather.R
import com.defendend.weather.databinding.FragmentWeatherListBinding

class WeatherListFragment : Fragment() {

    private val binding by viewBinding(FragmentWeatherListBinding::bind)
    private val viewModel by viewModels<WeatherListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewPager.adapter = WeatherPagerAdapter(this@WeatherListFragment)
            indicator.setViewPager(viewPager)
        }
    }


    companion object {
        fun newInstance(): WeatherListFragment {
            return WeatherListFragment()
        }
    }
}